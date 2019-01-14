package io.github.shopee.idata.pcprpc

import io.github.shopee.idata.klog.KLog
import java.io.{ PrintWriter, StringWriter }
import io.github.shopee.idata.saio.{ ConnectionHandler }
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec

case class PackageProtocol(headerLen: Int = 10) {
  private var bufferBuilder = new StringBuilder

  def sendPackage(conn: ConnectionHandler, text: String) =
    conn.sendMessage(textToPktMsg(text))

  def textToPktMsg(text: String) = {
    val lenText = text.length.toString()

    if (lenText.length > headerLen) { // digits
      throw new Exception("package size is out of limit.")
    }

    ("0" * (headerLen - lenText.length) + lenText) + text
  }

  def getPktText(data: Array[Byte]): List[String] =
    this.synchronized {
      bufferBuilder.append(new String(data, "UTF-8"))

      try {
        getPkt(ListBuffer[String]()).toList
      } catch {
        case e: Exception => {
          // clear builder
          bufferBuilder = new StringBuilder
          KLog.logErr(
            "pkt-parse-error",
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
    if (bufferBuilder.length < headerLen) None
    else {
      val header  = bufferBuilder.substring(0, headerLen)
      val bodyLen = header.toInt
      val pktLen  = bodyLen + headerLen

      if (bufferBuilder.length >= pktLen) {
        val pkt = bufferBuilder.substring(headerLen, pktLen)
        // update buffer
        // buffer = buffer.slice(pktLen, buffer.length)
        bufferBuilder = new StringBuilder(bufferBuilder.substring(pktLen, bufferBuilder.length))
        Some(pkt)
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
