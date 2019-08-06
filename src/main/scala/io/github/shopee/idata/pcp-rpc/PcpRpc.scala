package io.github.free.lock.pcprpc

import io.github.free.lock.saio.{ AIO, AIOConnection, ConnectionHandler }
import io.github.free.lock.pcp.{ BoxFun, CallResult, Sandbox }
import reflect.runtime.universe._
import io.github.free.lock.sjson.JSON
import scala.concurrent.{ ExecutionContext, Future }
import io.github.free.lock.spool.{ Item, Pool }
import io.github.free.lock.pcpstream.{ StreamClient, StreamServer }

object PcpRpc {
  val STREAM_ACCEPT_NAME = "__stream_accept"

  // use context to generate sandbox
  type GenerateSandbox = (StreamServer[Future[_]]) => Sandbox
  type OnClose         = (Exception) => _

  val defOnClose = (e: Exception) => {}
  val defGenerateSandbox = (streamServer: StreamServer[Future[_]]) =>
    new Sandbox(Map[String, BoxFun]())

  def GetPcpConnectionHandlerFromTcpConn(
      generateSandbox: GenerateSandbox,
      connection: AIOConnection.Connection,
      onClose: OnClose = defOnClose
  )(implicit ec: ExecutionContext): ComputingConnection.PureCallConnection = {
    // create stream object
    val streamClient = StreamClient()

    lazy val streamServer: StreamServer[Future[_]] = StreamServer[Future[_]](
      STREAM_ACCEPT_NAME,
      (command: String, timeout: Int) => pcpConnection.callRemote(command, timeout)
    )

    lazy val pcpConnection = ComputingConnection.createPureCallHandler(
      connection = connection,
      sandbox = new Sandbox(
        Map[String, BoxFun](
          STREAM_ACCEPT_NAME -> streamClient.getPcpStreamAcceptBoxFun()
        ) ++ generateSandbox(streamServer).funMap
      ),
      onClose = (err: Exception) => {
        // clean stream client when connection closed
        streamClient.clean()
        onClose(err)
      },
      streamClient = streamClient
    )

    pcpConnection
  }

  def getPCHighServer(
      hostname: String = "0.0.0.0",
      port: Int = 0,
      generateSandbox: GenerateSandbox
  )(implicit ec: ExecutionContext) =
    AIO.getTcpServer(hostname, port, (connection: AIOConnection.Connection) => {
      GetPcpConnectionHandlerFromTcpConn(generateSandbox, connection)
    })

  def getPCServer(
      hostname: String = "0.0.0.0",
      port: Int = 0,
      sandbox: Sandbox
  )(implicit ec: ExecutionContext) =
    getPCHighServer(hostname, port, (ss: StreamServer[Future[_]]) => sandbox)

  def getPCClient(
      hostname: String = "localhost",
      port: Int = 8000,
      generateSandbox: GenerateSandbox = defGenerateSandbox,
      onClose: OnClose = defOnClose
  )(
      implicit ec: ExecutionContext
  ): Future[ComputingConnection.PureCallConnection] =
    AIO.getTcpClient(hostname, port) map { conn =>
      GetPcpConnectionHandlerFromTcpConn(generateSandbox, conn, onClose)
    }

  // try to use pool to manage
  case class ServerAddress(host: String = "localhost", port: Int)
  // can get server address dynamicly in a pool
  type GetServerAddress = () => Future[ServerAddress]

  case class ClientPool(
      getServerAddress: GetServerAddress,
      generateSandbox: GenerateSandbox = defGenerateSandbox,
      RETRY_TIME: Int = 2000,
      POOL_SIZE: Int = 8
  )(implicit ec: ExecutionContext) {
    private def getNewConnection(
        onClose: () => _
    ): Future[Item[ComputingConnection.PureCallConnection]] =
      // 1st step: dynamically get the ip-port
      getServerAddress() flatMap { serverAddress =>
        getPCClient(
          hostname = serverAddress.host,
          port = serverAddress.port,
          generateSandbox = generateSandbox,
          onClose = (e: Exception) => onClose()
        ) map { client =>
          Item(resource = client, clean = client.close)
        }
      }

    private val pool = Pool[ComputingConnection.PureCallConnection](getNewConnection, POOL_SIZE)

    def clean() = pool.stopPool()

    def call(
        list: CallResult,
        timeout: Int = 2 * 60 * 1000,
        waitingTime: Int = 10 * 1000
    ): Future[_] =
      pool.useItem[Any]((client) => client.call(list, timeout), waitingTime)

    def callTo[T: TypeTag](list: CallResult, timeout: Int = 120000, waitingTime: Int = 10 * 1000) =
      call(list, timeout, waitingTime) map { ret =>
        JSON.convert[T](ret)
      }
  }

  def getPCClientPool(
      getServerAddress: GetServerAddress,
      generateSandbox: GenerateSandbox = defGenerateSandbox,
      RETRY_TIME: Int = 2000,
      POOL_SIZE: Int = 8
  )(implicit ec: ExecutionContext) =
    ClientPool(getServerAddress, generateSandbox, RETRY_TIME, POOL_SIZE)
}
