package io.github.free.lock.pcprpc

import io.github.free.lock.pcpstream.{ StreamClient }
import io.github.free.lock.pcp.{ CallResult, PcpClient, PcpServer, Sandbox }
import java.io.{ PrintWriter, StringWriter }
import scala.concurrent.duration._
import java.util.UUID.randomUUID
import io.github.free.lock.saio.{ AIOConnection, ConnectionHandler }
import java.util.concurrent.ConcurrentHashMap
import scala.collection._
import scala.collection.convert.decorateAsScala._
import scala.annotation.tailrec
import io.github.free.lock.sjson.JSON
import io.github.free.lock.klog.KLog
import scala.concurrent.{ ExecutionContext, Future, Promise }

/**
  * 1. Node:
  *  (1) provide sandbox
  *  (2) handle chunks
  *
  * 2. call
  */
case class CommandData(text: Any, errno: Int = 0, errMsg: String = "")
case class CommandPkt(id: String, ctype: String, data: CommandData)

object ComputingConnection {
  val REQUEST_C_TYPE  = "purecall-request"
  val RESPONSE_C_TYPE = "purecall-response"

  private def getErrorMessage(err: Exception): String = {
    val sw = new StringWriter
    err.printStackTrace(new PrintWriter(sw))
    sw.toString
  }

  // package backend result into response command
  private def getResponseCommand(result: Future[_], id: String)(
      implicit ec: ExecutionContext
  ): Future[CommandPkt] =
    result map { item =>
      CommandPkt(id, RESPONSE_C_TYPE, new CommandData(item))
    } recover {
      case e: Exception => {
        CommandPkt(id, RESPONSE_C_TYPE, new CommandData("", 530, getErrorMessage(e)))
      }
    }

  private def stringToCommand(text: String) =
    try {
      JSON.parseTo[CommandPkt](text)
    } catch {
      case e: Exception => {
        KLog.logErr(
          "package-parsing-error",
          new Exception(
            s"Pkt parsing error. Error msg is '${e.getMessage}'. Pkt text is '${text}'"
          )
        )
        throw e
      }
    }

  private def commandToText(cmd: CommandPkt) =
    try {
      JSON.stringify(cmd)
    } catch {
      case e: Exception =>
        JSON.stringify(
          cmd.copy(
            data = CommandData(null, 521, s"fail to parse command to json string. ${cmd}")
          )
        )
    }

  private def executeRequestCommand(requestCommand: CommandPkt, pureCallServer: PcpServer)(
      implicit ec: ExecutionContext
  ): Future[_] =
    try {
      // execute command string
      pureCallServer.execute(requestCommand.data.text.asInstanceOf[String]) match {
        case v: Future[_] => v
        case v            => Future { v }
      }
    } catch {
      case e: Exception => {
        Future {
          throw e
        }
      }
    }

  case class PureCallConnection(
      connection: AIOConnection.Connection,
      onClose: (Exception) => _ = (e: Exception) => {},
      sandbox: Sandbox,
      streamClient: StreamClient
  )(implicit ec: ExecutionContext) {
    private val packageProtocol           = PackageProtocol()
    private val remoteCallMap             = new ConcurrentHashMap[String, Promise[Any]]().asScala
    private val pureCallServer: PcpServer = new PcpServer(sandbox)
    val pcpClient                         = new PcpClient()

    // notify
    private def handleResponsePkt(commandPkt: CommandPkt): Unit =
      if (remoteCallMap.contains(commandPkt.id)) {
        val p = remoteCallMap(commandPkt.id)
        if (commandPkt.data.errno == 0) {
          // resolve command data
          p trySuccess commandPkt.data.text
        } else {
          p tryFailure (new Exception(
            s"${commandPkt.data.errMsg} (${commandPkt.data.errno})"
          ))
        }
      } else {
        KLog.logErr(
          "missing-pkt-id",
          new Exception(s"can not find id ${commandPkt.id} for purecall response data.")
        )
        throw new Exception(s"can not find id ${commandPkt.id} for purecall response data.")
      }

    private val conn: ConnectionHandler = ConnectionHandler(
      connection = connection,
      onData = (chunk: Array[Byte]) => {
        packageProtocol.getPktText(chunk) map { text =>
          // none-blocking on data
          Future {
            val commandPkt = stringToCommand(text)

            commandPkt.ctype match {
              // handle request type package
              case REQUEST_C_TYPE =>
                getResponseCommand(
                  executeRequestCommand(commandPkt, pureCallServer),
                  commandPkt.id
                ) map commandToText map { text =>
                  packageProtocol.sendPackage(conn, text) recover {
                    case e: Exception => {
                      throw new Exception(s"fail to send package. ${e}")
                    }
                  }
                }
              // handle response type package
              case RESPONSE_C_TYPE =>
                handleResponsePkt(commandPkt)
              case _ =>
                KLog.logErr(
                  "unknown-pkt-type",
                  new Exception(s"unknown type of package. Type is ${commandPkt.ctype}")
                )
                throw new Exception(s"unknown type of package. Type is ${commandPkt.ctype}")
            }
          }
        }
      },
      onClose = onClose
    )

    def getConnectionHandler() = conn

    def close() = {
      remoteCallMap.clear()
      conn.close(null)
    }

    def call(list: CallResult, timeout: Int = 2 * 60 * 1000): Future[_] =
      callRemote(pcpClient.toJson(list), timeout)

    //construct command string by using purecall client
    def callRemote(command: String, timeout: Int): Future[_] = {
      // construct data package
      val id   = randomUUID().toString
      val data = CommandPkt(id, REQUEST_C_TYPE, new CommandData(command))

      val p = Promise[Any]()
      remoteCallMap(id) = p

      // send text to remote
      // TODO support streamming
      packageProtocol.sendPackage(conn, JSON.stringify(data)) flatMap { _ =>
        TimeoutScheduler.withTimeout(p.future, timeout / 1000 seconds) map { ret =>
          remoteCallMap.remove(id)
          ret
        }
      } recover {
        case e: Exception => {
          remoteCallMap.remove(id)
          throw e
        }
      }
    }
  }

  /**
    * create a connection handler and would proxy onData stream
    */
  def createPureCallHandler(
      connection: AIOConnection.Connection,
      onClose: (Exception) => _ = (e: Exception) => {},
      sandbox: Sandbox,
      streamClient: StreamClient
  )(implicit ec: ExecutionContext): PureCallConnection =
    PureCallConnection(connection, onClose, sandbox, streamClient)
}
