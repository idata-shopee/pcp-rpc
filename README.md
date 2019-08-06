# pcp-rpc

A scala RPC based on pcp protocol

## Quick Example

```scala
import io.github.free.lock.pcp.{ BoxFun, PcpClient, PcpServer, Sandbox }
import io.github.free.lock.pcprpc.PcpRpc
import scala.concurrent.ExecutionContext.Implicits.global

val sandbox = new Sandbox(
  Map[String, BoxFun](
    // define add function
    "add" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
      val a = params(0).asInstanceOf[Int]
      val b = params(1).asInstanceOf[Int]
      a + b
    }),
    "testFuture" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
      Future { 123 }
    }),
    "textException" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
      Future { throw new Exception("test rpc exception.") }
    })
  )
)

val server = PcpRpc.getPCServer(port = 5878, sandbox = sandbox)

PcpRpc.getPCClient(port = 5878) map { client =>
  val p = new PcpClient()
  client.call(p.call("add", 1, 2)) // Future {3}
}
```

## Pool client example

```scala
import io.github.free.lock.pcp.{ BoxFun, PcpClient, PcpServer, Sandbox }
import io.github.free.lock.pcprpc.PcpRpc
import scala.concurrent.ExecutionContext.Implicits.global

val sandbox = new Sandbox(
  Map[String, BoxFun](
    // define add function
    "add" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
      val a = params(0).asInstanceOf[Int]
      val b = params(1).asInstanceOf[Int]
      a + b
    }),
    "testFuture" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
      Future { 123 }
    }),
    "textException" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
      Future { throw new Exception("test rpc exception.") }
    })
  )
)

val server = PcpRpc.getPCServer(port = 5878, sandbox = sandbox)

val pool = PcpRpc.getPCClientPool(
  getServerAddress = () => Future { PcpRpc.ServerAddress(port = 5878) }
)
pool.call(p.call("add", 1, 2)) // Future {3}
```
