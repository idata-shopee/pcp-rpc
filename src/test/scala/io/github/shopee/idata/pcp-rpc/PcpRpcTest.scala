package io.github.shopee.idata.pcprpc

import io.github.shopee.idata.taskqueue.TimeoutScheduler
import io.github.shopee.idata.pcp.{ BoxFun, CallResult, PcpClient, PcpServer, Sandbox }
import java.net.InetSocketAddress
import io.github.shopee.idata.sjson.JSON
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future, Promise, duration }
import duration._
import scala.io

class PcpRpcTest extends org.scalatest.FunSuite {
  val BIG_STRING = (1 to 10000 map { _ =>
    "hhhhsalkdksdk"
  }).mkString("")
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
      }),
      "bigConfig" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
        Future {
          JSON.parse(io.Source.fromResource("test.json").getLines.toList.mkString(""))
        }
      }),
      "bigString" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
        BIG_STRING
      })
    )
  )

  def testCallRpcServer(
      list: CallResult,
      expect: Any,
      clientNum: Int = 20,
      poolReqCount: Int = 30,
      timeout: Int = 30
  ) = {
    val server = PcpRpc.getPCServer(generateSandbox = (_) => sandbox)

    val clientCall = () => {
      val pool = PcpRpc.getPCClientPool(
        getServerAddress = () => Future { PcpRpc.ServerAddress(port = server.getPort()) },
        generateSandbox = (_) => new Sandbox(Map[String, BoxFun]())
      )
      Future.sequence((1 to poolReqCount map { _ =>
        pool.call(list) map { result =>
          assert(result == expect)
        }
      }).toList) map { _ =>
        pool.clean()
      } recover {
        case e: Exception => {
          pool.clean()
          throw e
        }
      }
    }

    try {
      Await.result(Future.sequence(1 to clientNum map { _ =>
        clientCall()
      }), timeout.seconds)
    } finally {
      server.close()
    }
  }

  def testCallRpcServerFail(list: CallResult, clientNum: Int = 20, poolReqCount: Int = 100) = {
    val server = PcpRpc.getPCServer(generateSandbox = (_) => sandbox)

    val clientCall = () => {
      val pool = PcpRpc.getPCClientPool(
        getServerAddress = () => Future { PcpRpc.ServerAddress(port = server.getPort()) },
        generateSandbox = (_) => new Sandbox(Map[String, BoxFun]())
      )

      val client = Await.result(
        PcpRpc.getPCClient(
          port = server.getPort(),
          generateSandbox = (_) => new Sandbox(Map[String, BoxFun]())
        ),
        15.seconds
      )

      Future.sequence((1 to poolReqCount map { _ =>
        var count = 0
        pool.call(list) recover {
          case e: Exception => {
            count += 1
          }
        } map { _ =>
          assert(count == 1)
        }
      }).toList) map { _ =>
        pool.clean()
      } recover {
        case e: Exception => {
          pool.clean()
          throw e
        }
      }
    }

    try {
      Await.result(Future.sequence(1 to clientNum map { _ =>
        clientCall()
      }), 15.seconds)
    } finally {
      server.close()
    }
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

  test("missing box function") {
    val p = new PcpClient()
    testCallRpcServerFail(p.call("fakkkkkkkkkk"))
  }

  test("big config") {
    val p = new PcpClient()
    testCallRpcServerFail(p.call("bigConfig"), poolReqCount = 100)
  }

  test("big string") {
    val p = new PcpClient()
    testCallRpcServer(p.call("bigString"), BIG_STRING)
  }
}
