package io.github.shopee.idata.pcprpc

import io.github.shopee.idata.saio.{ AIO, AIOConnection, ConnectionHandler }
import io.github.shopee.idata.pcp.{ BoxFun, Sandbox }
import scala.concurrent.{ ExecutionContext, Future }

object PcpRpc {
  def getPCServer(
      hostname: String = "0.0.0.0",
      port: Int = 0,
      sandbox: Sandbox
  )(implicit ec: ExecutionContext) =
    AIO.getTcpServer(hostname, port, (connection: AIOConnection.Connection) => {
      ComputingConnection.createPureCallHandler(connection = connection, sandbox = sandbox)
    })

  def getPCClient(
      hostname: String = "localhost",
      port: Int = 8000,
      onClose: (Exception) => _ = (e: Exception) => {},
  )(
      implicit ec: ExecutionContext
  ): Future[ComputingConnection.PureCallConnection] =
    AIO.getTcpClient(hostname, port) map { conn =>
      ComputingConnection.createPureCallHandler(connection = conn,
                                                onClose = onClose,
                                                sandbox = new Sandbox(Map[String, BoxFun]()))
    }
}
