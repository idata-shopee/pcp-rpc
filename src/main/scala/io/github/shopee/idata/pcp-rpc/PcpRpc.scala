package io.github.shopee.idata.pcprpc

import io.github.shopee.idata.saio.{ AIO, AIOConnection, ConnectionHandler }
import io.github.shopee.idata.pcp.{ BoxFun, CallResult, Sandbox }
import reflect.runtime.universe._
import io.github.shopee.idata.sjson.JSON
import scala.concurrent.{ ExecutionContext, Future }
import io.github.shopee.idata.spool.{ Item, Pool }

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

  // try to use pool to manage
  case class ServerAddress(host: String = "localhost", port: Int)
  // can get server address dynamicly in a pool
  type GetServerAddress = () => Future[ServerAddress]

  case class ClientPool(getServerAddress: GetServerAddress,
                        sandbox: Sandbox = new Sandbox(Map[String, BoxFun]()),
                        RETRY_TIME: Int = 2000,
                        POOL_SIZE: Int = 8)(implicit ec: ExecutionContext) {
    private def getNewConnection(
        onClose: () => _
    ): Future[Item[ComputingConnection.PureCallConnection]] =
      // 1st step: dynamically get the ip-port
      getServerAddress() flatMap { serverAddress =>
        getPCClient(hostname = serverAddress.host,
                    port = serverAddress.port,
                    onClose = (e: Exception) => onClose()) map { client =>
          Item(resource = client, clean = client.close)
        }
      }

    private val pool = Pool[ComputingConnection.PureCallConnection](getNewConnection, POOL_SIZE)

    def clean() = pool.stopPool()

    def call(list: CallResult,
             timeout: Int = 2 * 60 * 1000,
             waitingTime: Int = 10 * 1000): Future[_] =
      pool.useItem[Any]((client) => client.call(list, timeout), waitingTime)

    def callTo[T: TypeTag](list: CallResult, timeout: Int = 120000, waitingTime: Int = 10 * 1000) =
      call(list, timeout, waitingTime) map { ret =>
        JSON.convert[T](ret)
      }
  }

  def getPCClientPool(getServerAddress: GetServerAddress,
                      sandbox: Sandbox = new Sandbox(Map[String, BoxFun]()),
                      RETRY_TIME: Int = 2000,
                      POOL_SIZE: Int = 8)(implicit ec: ExecutionContext) =
    ClientPool(getServerAddress, sandbox, RETRY_TIME, POOL_SIZE)
}
