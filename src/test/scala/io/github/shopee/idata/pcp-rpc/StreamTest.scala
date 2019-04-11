package io.github.shopee.idata.pcprpc

import io.github.shopee.idata.pcp.{ BoxFun, CallResult, PcpClient, PcpServer, Sandbox }
import io.github.shopee.idata.pcpstream.{ PcpStream, StreamProducer, StreamServer }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future, Promise, duration }
import duration._

class StreamTest extends org.scalatest.FunSuite {
  test("stream: base") {
    val server = PcpRpc.getPCServer(generateSandbox = (streamServer: StreamServer[Future[_]]) => {
      new Sandbox(
        Map[String, BoxFun](
          "streamApi" -> streamServer.streamApi(
            (streamProducer: StreamProducer[Future[_]], params: List[Any], pcpServer: PcpServer) => {
              val seed = params(0).asInstanceOf[String]

              streamProducer.sendData(seed + "1") map { _ =>
                streamProducer.sendData(seed + "2") map { _ =>
                  streamProducer.sendData(seed + "3") map { _ =>
                    streamProducer.sendEnd()
                  }
                }
              }
            }
          )
        )
      )
    })

    val p = Promise[Any]()

    var result = ""
    PcpRpc.getPCClient(
      port = server.getPort()
    ) map { client =>
      client.call(client.streamClient.streamCall("streamApi", "_", (t: Int, d: Any) => {
        if (t == PcpStream.STREAM_DATA) {
          result += d.asInstanceOf[String]
        } else if (t == PcpStream.STREAM_END) {
          p.trySuccess(1)
        }
      }))
    }

    Await.result(p.future, 1.second)
    assert(result == "_1_2_3")
  }
}
