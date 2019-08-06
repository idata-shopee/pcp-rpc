name := "pcp-rpc"
organization := "io.github.lock-free"
version := "0.0.10"
scalaVersion := "2.12.4"

useGpg := true 
parallelExecution in Test := true

publishTo := sonatypePublishTo.value

libraryDependencies ++= Seq(
  // Log lib
  "io.github.lock-free" %% "klog" % "0.1.0",

  // JSON lib
  "io.github.lock-free" %% "sjson" % "0.2.0",

  // pcp protocol
  "io.github.lock-free" %% "pcp" % "0.1.1",
  "io.github.lock-free" %% "pcp-stream" % "0.0.2",

  // saio
  "io.github.lock-free" %% "saio" % "0.0.2",

  "io.netty" % "netty" % "3.7.0.Final",

  // spool lib
  "io.github.lock-free" %% "spool" % "0.0.1",

  // test suite
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)
