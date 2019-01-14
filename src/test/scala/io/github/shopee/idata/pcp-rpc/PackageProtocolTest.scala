package io.github.shopee.idata.pcprpc

class PackageProtocolTest extends org.scalatest.FunSuite {
  test("textToPktMsg") {
    val packageProtocol = PackageProtocol()

    assert(packageProtocol.textToPktMsg("hello world!") == "0000000012hello world!")
  }

  test("getPktText") {
    val packageProtocol = PackageProtocol()
    val text            = "hello, world! Should be longerrrrrrr."

    val pktSent = packageProtocol.textToPktMsg(text)
    val part1   = pktSent.slice(0, 10)
    val part2   = pktSent.slice(10, pktSent.length)

    assert(packageProtocol.getPktText(part1.getBytes()) == List())
    assert(packageProtocol.getPktText(part2.getBytes()) == List(text))
  }

  test("getPktText2") {
    val packageProtocol = PackageProtocol()
    val text1           = "hello, world! Should be longerrrrrrr."
    val text2           = "222222222lv$jjreio"

    val pktSent1 = packageProtocol.textToPktMsg(text1)
    val pktSent2 = packageProtocol.textToPktMsg(text2)
    val part1    = pktSent1.slice(0, 10)
    val part2    = pktSent1.slice(10, pktSent1.length)
    val part3    = pktSent2.slice(0, 12)
    val part4    = pktSent2.slice(12, pktSent2.length)

    1 to 100 map { _ =>
      // [1,2,3,4]
      assert(
        packageProtocol.getPktText((part1 + part2 + part3 + part4).getBytes()) == List(text1, text2)
      )

      // [1,2,3] 4
      assert(packageProtocol.getPktText((part1 + part2 + part3).getBytes()) == List(text1))
      assert(packageProtocol.getPktText((part4).getBytes()) == List(text2))

      // 1, [2,3], 4
      assert(packageProtocol.getPktText(part1.getBytes()) == List())
      assert(packageProtocol.getPktText((part2 + part3).getBytes()) == List(text1))
      assert(packageProtocol.getPktText(part4.getBytes()) == List(text2))
    }
  }
}
