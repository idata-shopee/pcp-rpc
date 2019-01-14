package io.github.shopee.idata.pcprpc

import io.github.shopee.idata.pcp.{ BoxFun, PcpServer, Sandbox, PcpClient }
import java.net.InetSocketAddress
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future, Promise, duration }
import duration._

class PcpRpcTest extends org.scalatest.FunSuite {
  val sandbox = new Sandbox(
    Map[String, BoxFun](
      // define add function
      "add" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
        val a = params(0).asInstanceOf[Int]
        val b = params(1).asInstanceOf[Int]
        a + b
      })
    )
  )

  test("base") {
    val server = PcpRpc.getPCServer(sandbox = sandbox)
    val p = new PcpClient()

    val client = Await.result(PcpRpc.getPCClient(port = server.getLocalAddress().asInstanceOf[InetSocketAddress].getPort()), 15.seconds)

    assert(Await.result(client.call(p.call("add", 1, 2)), 15.seconds) == 3)

    server.close()
  }
}
