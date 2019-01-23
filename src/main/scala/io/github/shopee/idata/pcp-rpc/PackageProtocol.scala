package io.github.shopee.idata.pcprpc

import io.github.shopee.idata.klog.KLog
import java.nio.ByteBuffer
import java.io.{ PrintWriter, StringWriter }
import io.github.shopee.idata.saio.{ ConnectionHandler }
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.collection.mutable.SynchronizedQueue
import java.util.concurrent.atomic.{ AtomicBoolean }

case class PackageProtocol() {

  /**
    * Bytes: 0          1     2    3    4
    *      version     [    Long Size    ]
    */
  val headerLen: Int = 5

  case class PktPromise(data: Array[Byte], p: Promise[Any])
  private val pktQueue = new SynchronizedQueue[PktPromise]()

  def sendPackage(conn: ConnectionHandler,
                  text: String)(implicit ec: ExecutionContext): Future[Any] = synchronized {
    val p   = Promise[Any]
    val pkt = textToPkt(text)
    pktQueue.enqueue(PktPromise(pkt, p))
    consumePkts(conn: ConnectionHandler)
    p.future
  }

  val isProcessing = new AtomicBoolean(false)

  private def consumePkts(conn: ConnectionHandler)(implicit ec: ExecutionContext): Unit =
    // get lock and lock on
    if (isProcessing.compareAndSet(false, true)) {
      if (pktQueue.length > 0) {
        val item = pktQueue.dequeue()

        conn.sendBytes(ByteBuffer.wrap(item.data)) map { v =>
          item.p trySuccess v
          isProcessing.set(false) // release

          consumePkts(conn)
        } recover {
          case e: Exception =>
            item.p tryFailure e
            KLog.logErr("send-package-error", e)
            isProcessing.set(false) // release
            consumePkts(conn)
        }
      } else {
        isProcessing.set(false) // release
      }
    }

  def textToPkt(text: String): Array[Byte] = {
    val textBytes      = text.getBytes("UTF-8")
    val lenOfTextBytes = textBytes.length

    // version + length + reserved + textBytes
    Array[Byte](0) ++ ByteBuffer.allocate(4).putInt(lenOfTextBytes).array() ++ textBytes
  }

  private var bufferBuilder = ListBuffer[Byte]()

  def getPktText(data: Array[Byte]): List[String] =
    this.synchronized {
      // put data into buffer
      data.foreach((byte) => {
        bufferBuilder.append(byte)
      })

      try {
        getPkt(ListBuffer[String]()).toList
      } catch {
        case e: Exception => {
          // clear builder
          bufferBuilder = ListBuffer[Byte]()
          KLog.logErr(
            "unpkt-error",
            new Exception(
              s"parse pkt fail. ErrMsg: ${getErrorMessage(e)}. Text: ${new String(data, "UTF-8")}"
            )
          )
          throw e
        }
      }
    }

  @tailrec
  private def getPkt(result: ListBuffer[String]): ListBuffer[String] =
    getSinglePkt() match {
      case None => result
      case Some(pkt) =>
        result.append(pkt)
        getPkt(result)
    }

  private def getSinglePkt(): Option[String] =
    if (bufferBuilder.length <= headerLen) None
    else {
      // TODO header version
      // index 1 to 5 bytes as pkt length
      val bodyLen = ByteBuffer.wrap(bufferBuilder.slice(1, 5).toArray).getInt()
      val pktLen  = headerLen + bodyLen

      if (bufferBuilder.length >= pktLen) { // pkt all recieved
        val pkt = bufferBuilder.slice(headerLen, pktLen.toInt)
        // update buffer
        bufferBuilder = bufferBuilder.slice(pktLen.toInt, bufferBuilder.length)
        Some(new String(pkt.toArray, "UTF-8"))
      } else {
        None
      }
    }

  private def getErrorMessage(err: Exception): String = {
    val sw = new StringWriter
    err.printStackTrace(new PrintWriter(sw))
    sw.toString
  }
}
