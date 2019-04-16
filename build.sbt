name := "pcp-rpc"
organization := "io.github.idata-shopee"
version := "0.0.10"
scalaVersion := "2.12.4"

useGpg := true 
parallelExecution in Test := true

publishTo := sonatypePublishTo.value

libraryDependencies ++= Seq(
  // Log lib
  "io.github.idata-shopee" %% "klog" % "0.1.0",

  // taskqueue
  "io.github.idata-shopee" %% "taskqueue" % "0.1.0",

  // JSON lib
  "io.github.idata-shopee" %% "sjson" % "0.1.5",

  // pcp protocol
  "io.github.idata-shopee" %% "pcp" % "0.1.1",
  "io.github.idata-shopee" %% "pcp-stream" % "0.0.2",

  // saio
  "io.github.idata-shopee" %% "saio" % "0.0.2",

  // spool lib
  "io.github.idata-shopee" %% "spool" % "0.0.1",

  // test suite
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)
