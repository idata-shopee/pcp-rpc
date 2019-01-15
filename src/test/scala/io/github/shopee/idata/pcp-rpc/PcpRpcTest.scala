package io.github.shopee.idata.pcprpc

import io.github.shopee.idata.taskqueue.TimeoutScheduler
import io.github.shopee.idata.pcp.{ BoxFun, CallResult, PcpClient, PcpServer, Sandbox }
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
      }),
      "testFuture" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
        TimeoutScheduler.sleep(100) map { _ =>
          123
        }
      }),
      "textException" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
        TimeoutScheduler.sleep(100) map { _ =>
          throw new Exception("test rpc exception.")
        }
      })
    )
  )

  def testCallRpcServer(list: CallResult, expect: Any, clientNum: Int = 100) = {
    val server = PcpRpc.getPCServer(sandbox = sandbox)

    val clientCall = () => {
      val client = Await.result(
        PcpRpc.getPCClient(
          port = server.getLocalAddress().asInstanceOf[InetSocketAddress].getPort()
        ),
        15.seconds
      )
      client.call(list) map { result =>
        assert(result == expect)
      }
    }

    Await.result(Future.sequence(1 to clientNum map { _ =>
      clientCall()
    }), 15.seconds)

    server.close()
  }

  def testCallRpcServerFail(list: CallResult, clientNum: Int = 100) = {
    val server = PcpRpc.getPCServer(sandbox = sandbox)

    val clientCall = () => {
      val client = Await.result(
        PcpRpc.getPCClient(
          port = server.getLocalAddress().asInstanceOf[InetSocketAddress].getPort()
        ),
        15.seconds
      )
      var count = 0
      client.call(list) recover {
        case e: Exception => {
          count += 1
        }
      } map { _ =>
        assert(count == 1)
      }
    }

    Await.result(Future.sequence(1 to clientNum map { _ =>
      clientCall()
    }), 15.seconds)

    server.close()
  }

  test("base") {
    val p = new PcpClient()
    testCallRpcServer(p.call("add", 1, 2), 3)
  }

  test("future") {
    val p = new PcpClient()
    testCallRpcServer(p.call("testFuture"), 123)
  }

  test("exception") {
    val p = new PcpClient()
    testCallRpcServerFail(p.call("testException"))
  }
}
