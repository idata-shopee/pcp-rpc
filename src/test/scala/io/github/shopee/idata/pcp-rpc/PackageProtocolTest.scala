package io.github.shopee.idata.pcprpc

class PackageProtocolTest extends org.scalatest.FunSuite {
  test("textToPkt") {
    val packageProtocol = PackageProtocol()

    assert(Array[Byte](0, 0, 0, 0, 5, 104, 101, 108, 108, 111).toList == packageProtocol.textToPkt("hello").toList)
    val text = "hello world!"
    assert(
      packageProtocol.textToPkt(text).toList == (Array(0, 0, 0, 0, text.length) ++ text
        .getBytes("UTF-8")).toList
    )
  }

  test("getPktText") {
    val packageProtocol = PackageProtocol()
    val text            = "hello, world! Should be longerrrrrrr."

    val pktSent = packageProtocol.textToPkt(text)
    val part1   = pktSent.slice(0, 10)
    val part2   = pktSent.slice(10, pktSent.length)

    assert(packageProtocol.getPktText(part1) == List())
    assert(packageProtocol.getPktText(part2) == List(text))
  }
}
