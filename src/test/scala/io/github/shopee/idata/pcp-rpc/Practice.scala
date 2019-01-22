package io.github.shopee.idata.pcprpc

import io.github.shopee.idata.taskqueue.TimeoutScheduler
import io.github.shopee.idata.pcp.{ BoxFun, CallResult, PcpClient, PcpServer, Sandbox }
import java.net.InetSocketAddress
import io.github.shopee.idata.sjson.JSON
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future, Promise, duration }
import duration._
import scala.io

class PracticeTest extends org.scalatest.FunSuite {
  val BIG_STRING = s"""
  {"id":"3389a835-112c-4753-b161-34b39c6e3ad9","ctype":"purecall-response","data":{"text":{"headers":["Product View","Item ID","shop_name","Item Name","username","Level3 Category","item_stock","userid","Impression","Numbers of Likes","Main Category","Sub Category","Item GMV","shopid","Latest Item Discount Rate","is_official_store"],"data":[{"Product View":51,"Item ID":1542930301,"shop_name":"ABC日貨代購","Item Name":"【限量特價】【果實30%增量款】日清燕麥片 日清早餐脆片 日清 奢侈果實 草莓 大包裝 500g 水果燕麥脆片 燕麥脆片","username":"johnson75","Level3 Category":"Oatmeal & Cereals","item_stock":25,"userid":18026518,"Impression":993,"Numbers of Likes":2,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":18025182,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":2,"Item ID":114880885,"shop_name":"yangshuhsiu","Item Name":"美麗樂 即溶豆歌","username":"yangshuhsiu","Level3 Category":"Oatmeal & Cereals","item_stock":5,"userid":1575925,"Impression":0,"Numbers of Likes":0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":1574762,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":81,"Item ID":1161431294,"shop_name":"AJ 日韓小舖","Item Name":"【AJ日韓小舖】日本 日清 Nissin BIG 早餐玉米片 220g (多款可選) -TC","username":"aj1728","Level3 Category":"Oatmeal & Cereals","item_stock":600,"userid":12111458,"Impression":1527,"Numbers of Likes":15,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":12110156,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":134,"Item ID":1079285325,"shop_name":"⭐️平銘小舖⭐️","Item Name":"⭐️平銘小舖⭐️500g 彩虹藜麥 1:1:1 三色藜麥 多項檢驗報告 黑藜麥 紅藜麥 白藜麥","username":"qaz388388","Level3 Category":"Oatmeal & Cereals","item_stock":3700,"userid":3020599,"Impression":1953,"Numbers of Likes":34,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":1039.7993297025555,"shopid":3019312,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":1,"Item ID":882847130,"shop_name":"台灣合迷雅好物商城","Item Name":"桂格三合一麥片-低糖10包入【台灣合迷雅好物商城】","username":"didimai0918","Level3 Category":"Oatmeal & Cereals","item_stock":100,"userid":4798293,"Impression":45,"Numbers of Likes":1,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":4797003,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":6,"Item ID":1465997259,"shop_name":"東洋果子店《慶昌行》","Item Name":"【東洋果子店】維吉思 葡萄穀物脆果(170g) ．美國原裝進口．850529004467","username":"phoenix63226","Level3 Category":"Oatmeal & Cereals","item_stock":88,"userid":8255609,"Impression":154,"Numbers of Likes":0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":8254313,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":18,"Item ID":1751178490,"shop_name":"養生堂中藥行","Item Name":"【養生堂】小麥纖維 體內環保 排便順暢 養顏美�� 《現貨》","username":"kani0520","Level3 Category":"Oatmeal & Cereals","item_stock":14,"userid":17665810,"Impression":222,"Numbers of Likes":0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":17664474,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":7,"Item ID":72773760,"shop_name":"聖誕特賣 歡慶2019","Item Name":"~北國限時特賣!日本上班族ol必備品~ eleki易利氣 磁力肩帶磁石 永久磁石肩帶 搭配項圈更好用","username":"foolchenyuwen","Level3 Category":"Oatmeal & Cereals","item_stock":5,"userid":4854102,"Impression":294,"Numbers of Likes":35,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":4852812,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":5,"Item ID":1660778026,"shop_name":"fuaseangrocery","Item Name":"ENERGEN CEREAL DRINK CHOCOLATE 30g","username":"ootdbyirenefu","Level3 Category":"Oatmeal & Cereals","item_stock":19,"userid":61661941,"Impression":146,"Numbers of Likes":2,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":61660499,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":0,"Item ID":47920783,"shop_name":"走走看看就好","Item Name":"漢莫寧-綜合穀麥片300g","username":"sup9982003","Level3 Category":"Oatmeal & Cereals","item_stock":99,"userid":126976,"Impression":6,"Numbers of Likes":0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":126975,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":1,"Item ID":1646207621,"shop_name":"WelcomeLALALA","Item Name":"艾多美 綜合穀物飲 30入","username":"chengi1021","Level3 Category":"Oatmeal & Cereals","item_stock":9,"userid":21053628,"Impression":49,"Numbers of Likes":2,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":21052292,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":5,"Item ID":256868027,"shop_name":"3Q購","Item Name":"薌園 黑芝麻粉(熟粉)  (400公克/夾鏈袋裝)","username":"s3qgo","Level3 Category":"Oatmeal & Cereals","item_stock":656,"userid":21818900,"Impression":135,"Numbers of Likes":6,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":21817564,"Latest Item Discount Rate":0,"is_official_store":1},{"Product View":36,"Item ID":410706538,"shop_name":"茹素餐豐-素購網 (讓您吃素餐餐都豐富)","Item Name":"【茹素餐豐】尚緣玉米濃湯(奶素)32gX10包入 玉米濃湯 具有香濃與香甜滑順的口感，以天然食材研製而成，營養又可口！","username":"rusucfmall","Level3 Category":"Oatmeal & Cereals","item_stock":52,"userid":22266710,"Impression":533,"Numbers of Likes":34,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":139.04761904761904,"shopid":22265374,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":86,"Item ID":1653605529,"shop_name":"chiou0972","Item Name":"costco 代購 桂格黃金麥芽三合一麥片","username":"chiou0972","Level3 Category":"Oatmeal & Cereals","item_stock":3,"userid":12220565,"Impression":396,"Numbers of Likes":1,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":2424.0,"shopid":12219263,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":0,"Item ID":1141670510,"shop_name":"【喫健康】有機健康便利購","Item Name":"【喫健康】台灣綠源寶營養珍味杏仁燕麥奶(500g)/買6瓶可免運","username":"eathealthy41","Level3 Category":"Oatmeal & Cereals","item_stock":36,"userid":67578477,"Impression":64,"Numbers of Likes":0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":67577020,"Latest Item Discount Rate":0,"is_official_store":0}],"count":9316},"errno":0,"errMsg":""}}
  """
  val sandbox = new Sandbox(
    Map[String, BoxFun](
      // define add function
      "bigString" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
        BIG_STRING
      })
    )
  )

  test("string with special chars") {
    val server = PcpRpc.getPCServer(sandbox = sandbox)
    val pool = PcpRpc.getPCClientPool(
      getServerAddress = () => Future { PcpRpc.ServerAddress(port = server.getPort()) }
    )

    try {
      val p = new PcpClient()
      Await.result(pool.call(p.call("bigString")) map { result =>
        assert(result == BIG_STRING)
      }, 15.seconds)
    } finally {
      server.close()
    }
  }
}
