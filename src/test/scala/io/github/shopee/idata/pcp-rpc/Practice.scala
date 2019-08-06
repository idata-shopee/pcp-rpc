package io.github.free.lock.pcprpc

import io.github.free.lock.pcp.{ BoxFun, CallResult, PcpClient, PcpServer, Sandbox }
import java.net.InetSocketAddress
import io.github.free.lock.sjson.JSON
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future, Promise, duration }
import duration._
import scala.io

class PracticeTest extends org.scalatest.FunSuite {
  val BIG_STRING  = s"""
  {"id":"3389a835-112c-4753-b161-34b39c6e3ad9","ctype":"purecall-response","data":{"text":{"headers":["Product View","Item ID","shop_name","Item Name","username","Level3 Category","item_stock","userid","Impression","Numbers of Likes","Main Category","Sub Category","Item GMV","shopid","Latest Item Discount Rate","is_official_store"],"data":[{"Product View":51,"Item ID":1542930301,"shop_name":"ABC日貨代購","Item Name":"【限量特價】【果實30%增量款】日清燕麥片 日清早餐脆片 日清 奢侈果實 草莓 大包裝 500g 水果燕麥脆片 燕麥脆片","username":"johnson75","Level3 Category":"Oatmeal & Cereals","item_stock":25,"userid":18026518,"Impression":993,"Numbers of Likes":2,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":18025182,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":2,"Item ID":114880885,"shop_name":"yangshuhsiu","Item Name":"美麗樂 即溶豆歌","username":"yangshuhsiu","Level3 Category":"Oatmeal & Cereals","item_stock":5,"userid":1575925,"Impression":0,"Numbers of Likes":0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":1574762,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":81,"Item ID":1161431294,"shop_name":"AJ 日韓小舖","Item Name":"【AJ日韓小舖】日本 日清 Nissin BIG 早餐玉米片 220g (多款可選) -TC","username":"aj1728","Level3 Category":"Oatmeal & Cereals","item_stock":600,"userid":12111458,"Impression":1527,"Numbers of Likes":15,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":12110156,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":134,"Item ID":1079285325,"shop_name":"⭐️平銘小舖⭐️","Item Name":"⭐️平銘小舖⭐️500g 彩虹藜麥 1:1:1 三色藜麥 多項檢驗報告 黑藜麥 紅藜麥 白藜麥","username":"qaz388388","Level3 Category":"Oatmeal & Cereals","item_stock":3700,"userid":3020599,"Impression":1953,"Numbers of Likes":34,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":1039.7993297025555,"shopid":3019312,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":1,"Item ID":882847130,"shop_name":"台灣合迷雅好物商城","Item Name":"桂格三合一麥片-低糖10包入【台灣合迷雅好物商城】","username":"didimai0918","Level3 Category":"Oatmeal & Cereals","item_stock":100,"userid":4798293,"Impression":45,"Numbers of Likes":1,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":4797003,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":6,"Item ID":1465997259,"shop_name":"東洋果子店《慶昌行》","Item Name":"【東洋果子店】維吉思 葡萄穀物脆果(170g) ．美國原裝進口．850529004467","username":"phoenix63226","Level3 Category":"Oatmeal & Cereals","item_stock":88,"userid":8255609,"Impression":154,"Numbers of Likes":0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":8254313,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":18,"Item ID":1751178490,"shop_name":"養生堂中藥行","Item Name":"【養生堂】小麥纖維 體內環保 排便順暢 養顏美�� 《現貨》","username":"kani0520","Level3 Category":"Oatmeal & Cereals","item_stock":14,"userid":17665810,"Impression":222,"Numbers of Likes":0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":17664474,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":7,"Item ID":72773760,"shop_name":"聖誕特賣 歡慶2019","Item Name":"~北國限時特賣!日本上班族ol必備品~ eleki易利氣 磁力肩帶磁石 永久磁石肩帶 搭配項圈更好用","username":"foolchenyuwen","Level3 Category":"Oatmeal & Cereals","item_stock":5,"userid":4854102,"Impression":294,"Numbers of Likes":35,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":4852812,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":5,"Item ID":1660778026,"shop_name":"fuaseangrocery","Item Name":"ENERGEN CEREAL DRINK CHOCOLATE 30g","username":"ootdbyirenefu","Level3 Category":"Oatmeal & Cereals","item_stock":19,"userid":61661941,"Impression":146,"Numbers of Likes":2,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":61660499,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":0,"Item ID":47920783,"shop_name":"走走看看就好","Item Name":"漢莫寧-綜合穀麥片300g","username":"sup9982003","Level3 Category":"Oatmeal & Cereals","item_stock":99,"userid":126976,"Impression":6,"Numbers of Likes":0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":126975,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":1,"Item ID":1646207621,"shop_name":"WelcomeLALALA","Item Name":"艾多美 綜合穀物飲 30入","username":"chengi1021","Level3 Category":"Oatmeal & Cereals","item_stock":9,"userid":21053628,"Impression":49,"Numbers of Likes":2,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":21052292,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":5,"Item ID":256868027,"shop_name":"3Q購","Item Name":"薌園 黑芝麻粉(熟粉)  (400公克/夾鏈袋裝)","username":"s3qgo","Level3 Category":"Oatmeal & Cereals","item_stock":656,"userid":21818900,"Impression":135,"Numbers of Likes":6,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":21817564,"Latest Item Discount Rate":0,"is_official_store":1},{"Product View":36,"Item ID":410706538,"shop_name":"茹素餐豐-素購網 (讓您吃素餐餐都豐富)","Item Name":"【茹素餐豐】尚緣玉米濃湯(奶素)32gX10包入 玉米濃湯 具有香濃與香甜滑順的口感，以天然食材研製而成，營養又可口！","username":"rusucfmall","Level3 Category":"Oatmeal & Cereals","item_stock":52,"userid":22266710,"Impression":533,"Numbers of Likes":34,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":139.04761904761904,"shopid":22265374,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":86,"Item ID":1653605529,"shop_name":"chiou0972","Item Name":"costco 代購 桂格黃金麥芽三合一麥片","username":"chiou0972","Level3 Category":"Oatmeal & Cereals","item_stock":3,"userid":12220565,"Impression":396,"Numbers of Likes":1,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":2424.0,"shopid":12219263,"Latest Item Discount Rate":0,"is_official_store":0},{"Product View":0,"Item ID":1141670510,"shop_name":"【喫健康】有機健康便利購","Item Name":"【喫健康】台灣綠源寶營養珍味杏仁燕麥奶(500g)/買6瓶可免運","username":"eathealthy41","Level3 Category":"Oatmeal & Cereals","item_stock":36,"userid":67578477,"Impression":64,"Numbers of Likes":0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":0.0,"shopid":67577020,"Latest Item Discount Rate":0,"is_official_store":0}],"count":9316},"errno":0,"errMsg":""}}
  """
  val BIG_STRING2 = s"""
  {"id":"a9fe0117-a72f-437e-b09f-c0b3ad08b399","ctype":"purecall-response","data":{"text":{"headers":["Item ID","shop_name","Item Name","username","Level3 Category","item_stock","userid","Impression","Numbers of Likes","Latest Item Price","Main Category","Sub Category","Item GMV","shopid","Latest Item Discount Rate"],"data":[{"Item ID":1247404776,"shop_name":"挑食屋PIKIYA-日本進口食品專賣","Item Name":"【Kasugai春日井】金平糖 140g 日本進口糖果 挑食屋","username":"pikiyashop","Level3 Category":"Candy & Chocolate","item_stock":6,"userid":2066177,"Impression":151,"Numbers of Likes":0,"Latest Item Price":89.0,"Main Category":"Food & Beverages","Sub Category":"Snacks","Item GMV":0.0,"shopid":2064665,"Latest Item Discount Rate":18},{"Item ID":101549187,"shop_name":"貓咪姐妹 日本代購🇯🇵","Item Name":"日本 NISSIN 日清 Choco Flakes 可可玉米巧克力脆片(90g) 可可脆片 可可巧克力","username":"ruby03060306","Level3 Category":"Japanese Snacks","item_stock":7,"userid":1913089,"Impression":556,"Numbers of Likes":67,"Latest Item Price":60.0,"Main Category":"Food & Beverages","Sub Category":"Imported Snacks","Item GMV":312.2116689280868,"shopid":1911696,"Latest Item Discount Rate":0},{"Item ID":32132058,"shop_name":"味旅Spices Journey - 香辛料專賣店","Item Name":"【味旅嚴選】｜白胡椒粒｜100g","username":"bkyang","Level3 Category":"Sauces & Seasonings","item_stock":92,"userid":6193666,"Impression":572,"Numbers of Likes":101,"Latest Item Price":100.0,"Main Category":"Food & Beverages","Sub Category":"Baking & Grocery","Item GMV":616.21014818141,"shopid":6192359,"Latest Item Discount Rate":0},{"Item ID":1818911512,"shop_name":"貓咪姐妹 日本代購🇯🇵","Item Name":"日本 glico 格力高 固力果 草莓捲心餅 牛乳捲心餅 草莓夾心捲 牛乳餅乾 日本零食 進口零食","username":"ruby03060306","Level3 Category":"Japanese Snacks","item_stock":34,"userid":1913089,"Impression":366,"Numbers of Likes":2,"Latest Item Price":85.0,"Main Category":"Food & Beverages","Sub Category":"Imported Snacks","Item GMV":0.0,"shopid":1911696,"Latest Item Discount Rate":0},{"Item ID":1670953003,"shop_name":"挑食屋PIKIYA-日本進口食品專賣","Item Name":"【Nissin日清】UFO五目醬油海鮮炒麵 即食碗麵114g日本進口泡麵 挑食屋","username":"pikiyashop","Level3 Category":"Instant Noodle","item_stock":3,"userid":2066177,"Impression":952,"Numbers of Likes":4,"Latest Item Price":99.0,"Main Category":"Food & Beverages","Sub Category":"Rice & Noodles","Item GMV":0.0,"shopid":2064665,"Latest Item Discount Rate":20},{"Item ID":56825126,"shop_name":"貓咪姐妹 日本代購🇯🇵","Item Name":"日本一榮魷魚絲 墨魚絲鱈魚香絲單包(6g)【貓咪姐妹代購】魷魚絲 鱈魚絲","username":"ruby03060306","Level3 Category":"Japanese Snacks","item_stock":74,"userid":1913089,"Impression":1731,"Numbers of Likes":152,"Latest Item Price":10.0,"Main Category":"Food & Beverages","Sub Category":"Imported Snacks","Item GMV":424.40692877032006,"shopid":1911696,"Latest Item Discount Rate":0},{"Item ID":1625531861,"shop_name":"南榮xPickUpToU","Item Name":"【義美】巧克力酥片 一組2包入","username":"chianichilu","Level3 Category":"Traditional Snacks","item_stock":22,"userid":2931182,"Impression":534,"Numbers of Likes":6,"Latest Item Price":30.0,"Main Category":"Food & Beverages","Sub Category":"Snacks","Item GMV":29.223300970873787,"shopid":2929807,"Latest Item Discount Rate":0},{"Item ID":26418415,"shop_name":"貓咪姐妹 日本代購🇯🇵","Item Name":"日本 Kracie 知育果子 DIY自己動手做 食玩 漢堡食玩 壽司食玩 鯛魚丸子 壽司 冰淇淋 布丁 夏日慶典","username":"ruby03060306","Level3 Category":"Japanese Snacks","item_stock":84,"userid":1913089,"Impression":2531,"Numbers of Likes":640,"Latest Item Price":99.0,"Main Category":"Food & Beverages","Sub Category":"Imported Snacks","Item GMV":3061.9923954372625,"shopid":1911696,"Latest Item Discount Rate":10},{"Item ID":85407364,"shop_name":"貓咪姐妹 日本代購🇯🇵","Item Name":"日本 UHA味覺糖 噗啾糖果 蘇打藍莓葡萄���心軟糖 (98g) 哈密瓜蘇打軟糖 青蘋果蘇打 草莓軟糖 蘇打軟糖 鳳梨軟糖","username":"ruby03060306","Level3 Category":"Japanese Snacks","item_stock":90,"userid":1913089,"Impression":575,"Numbers of Likes":95,"Latest Item Price":79.0,"Main Category":"Food & Beverages","Sub Category":"Imported Snacks","Item GMV":0.0,"shopid":1911696,"Latest Item Discount Rate":0},{"Item ID":78966669,"shop_name":"味旅Spices Journey - 香辛料專賣店","Item Name":"【味旅嚴選】｜山艾粉｜50g/100g","username":"bkyang","Level3 Category":"Sauces & Seasonings","item_stock":197,"userid":6193666,"Impression":151,"Numbers of Likes":11,"Latest Item Price":70.0,"Main Category":"Food & Beverages","Sub Category":"Baking & Grocery","Item GMV":0.0,"shopid":6192359,"Latest Item Discount Rate":0},{"Item ID":1101840765,"shop_name":"挑食屋PIKIYA-日本進口食品專賣","Item Name":"【可口可樂】期間限定Coca-Cola鋁瓶裝原味可樂 250ml 收藏版 日本原裝進口","username":"pikiyashop","Level3 Category":"Drink","item_stock":15,"userid":2066177,"Impression":375,"Numbers of Likes":10,"Latest Item Price":75.0,"Main Category":"Food & Beverages","Sub Category":"Beverage","Item GMV":94.23076923076924,"shopid":2064665,"Latest Item Discount Rate":24},{"Item ID":62566073,"shop_name":"貓咪姐妹 日本代購🇯🇵","Item Name":"日本春日井KASUGAI芥末味脆皮青豌豆(80g)","username":"ruby03060306","Level3 Category":"Dried Fruit & Nuts","item_stock":22,"userid":1913089,"Impression":357,"Numbers of Likes":18,"Latest Item Price":65.0,"Main Category":"Food & Beverages","Sub Category":"Snacks","Item GMV":68.85185185185185,"shopid":1911696,"Latest Item Discount Rate":0},{"Item ID":1712439598,"shop_name":"挑食屋PIKIYA-日本進口食品專賣","Item Name":"【三幸製菓】期間限定 雪宿牛奶米果-甘酒風味 22枚入 145.2g 日本進口零食","username":"pikiyashop","Level3 Category":"Japanese Snacks","item_stock":3,"userid":2066177,"Impression":181,"Numbers of Likes":0,"Latest Item Price":89.0,"Main Category":"Food & Beverages","Sub Category":"Imported Snacks","Item GMV":0.0,"shopid":2064665,"Latest Item Discount Rate":25},{"Item ID":13699942,"shop_name":"南榮xPickUpToU","Item Name":"HAMADA鈣質威化餅 一組3入","username":"chianichilu","Level3 Category":"Cookies & Potato Chips & Popcorn","item_stock":37,"userid":2931182,"Impression":216,"Numbers of Likes":38,"Latest Item Price":20.0,"Main Category":"Food & Beverages","Sub Category":"Snacks","Item GMV":0.0,"shopid":2929807,"Latest Item Discount Rate":0},{"Item ID":345973797,"shop_name":"��咪姐妹 日本代購🇯🇵","Item Name":"日本 YAMAKI 亞媽吉 茶碗蒸鰹魚昆布高湯(45ml)","username":"ruby03060306","Level3 Category":"Sauces & Seasonings","item_stock":16,"userid":1913089,"Impression":769,"Numbers of Likes":40,"Latest Item Price":35.0,"Main Category":"Food & Beverages","Sub Category":"Baking & Grocery","Item GMV":92.97619047619047,"shopid":1911696,"Latest Item Discount Rate":36}],"count":753},"errno":0,"errMsg":"}}
  """
  val BIG_STRING3 = s"""
  {"id":"e7dd4a17-7c01-433b-80ec-875fa9285486","ctype":"purecall-response","data":{"text":{"headers":["Item Estimated Days To Ship","Item ID","shop_name","Item Name","username","userid","shopid"],"data":[{"Item Estimated Days To Ship":2,"Item ID":273764098,"shop_name":"SAS","Item Name":"SAS 大衣外套 中長大衣 毛尼大衣 毛呢大衣 繭型大衣 翻領大衣 翻領外套 中長版外套 西裝外套【181】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":20,"Item ID":446401449,"shop_name":"SAS","Item Name":"S.A.S 女襯衫 黑白格紋襯衫 韓版襯衫 上班襯衫 OL襯衫 女上班衣服 女商務襯衫 女休閒襯衫 女長袖襯衫【144】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":20,"Item ID":1696228863,"shop_name":"SAS","Item Name":"S.A.S 日系水玉點點襪 學生運動森系棉襪中筒襪 小清心森女襪子 【601】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":20,"Item ID":626503981,"shop_name":"SAS","Item Name":"SAS 絲絨百褶裙 百摺長裙 百折裙 光澤百褶裙 絲絨長裙 高腰裙 高腰長裙 光澤感長裙 金屬長裙【199】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":14,"Item ID":1696223690,"shop_name":"SAS","Item Name":"SAS 中筒側邊愛心襪 女襪短襪棉襪 愛心圖案短筒襪 日系心型襪 桃心襪子 【600】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":2,"Item ID":1211702503,"shop_name":"SAS","Item Name":"SAS 韓國ulzzang字母皮帶 反光字母刺繡皮帶 布面皮帶 刺繡腰帶 潮流皮帶 百搭男女學生雙環金屬皮帶【352】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":2,"Item ID":17140777,"shop_name":"SAS","Item Name":"SAS 草帽 基本款草帽 遮陽帽 大沿帽 海灘遮陽帽 大簷帽沙灘 太陽帽 草帽防曬 送飄帶 小孩/大人草帽 【016】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":2,"Item ID":1739131360,"shop_name":"SAS","Item Name":"S.A.S 小高領反折袖羊毛衣 韓版圓領針織衫 套頭毛衣 修身針織衫 純色毛衣 拼接毛衣 休閒毛衣 羊毛衫 【625】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":14,"Item ID":10915379,"shop_name":"SAS","Item Name":"SAS 安全褲 雕花鏤空花邊 防走光 春夏薄款防走光三分內搭褲 蕾絲��邊 裸空燒花可外穿 【012】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":2,"Item ID":1671112279,"shop_name":"SAS","Item Name":"S.A.S 純色小高領針織長袖上衣 基本款內搭 高領長袖上衣 內搭針織衫 秋冬圓領針織T恤 針織上衣 長袖上衣【569】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":14,"Item ID":967558976,"shop_name":"SAS","Item Name":"SAS 不規則短裙套裝 不規則包臀裙 短版上衣 緊身上衣 露肚上衣 鐵圈短裙 高腰短裙 顯瘦短裙【249】【250】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":20,"Item ID":1742633017,"shop_name":"SAS","Item Name":"SAS高領反折羊毛衣 套頭毛衣 毛衣 中長版毛衣 素色針織衫 高領毛衣 長袖高領毛衣針織衫長袖上衣百搭修身款 【631】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":14,"Item ID":216940392,"shop_name":"SAS","Item Name":"SAS 飛行外套 女飛行外套 棒球外套 薄外套 秋天外套 寬鬆外套 百搭外套 防風外套【166】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":14,"Item ID":10600284,"shop_name":"SAS","Item Name":"SAS 短絲襪 膚色絲襪 膚色短絲襪 防磨腳絲襪腳踝絲襪  短絲襪 短絲襪膚色 透膚絲襪短 【024】","username":"sas.taipei","userid":2757025,"shopid":2755743},{"Item Estimated Days To Ship":2,"Item ID":1671187231,"shop_name":"SAS","Item Name":"SAS 落肩潮流帽T 韓版字母連帽大學T 寬鬆學生風 BF風 男友風 保暖必備 長袖上衣 大碼可 【578】","username":"sas.taipei","userid":2757025,"shopid":2755743}],"count":134},"errno":0,"errMsg":""}}
  """
  val sandbox = new Sandbox(
    Map[String, BoxFun](
      // define add function
      "bigString" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
        BIG_STRING
      }),
      "bigString2" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
        BIG_STRING2
      }),
      "bigString3" -> Sandbox.toSanboxFun((params: List[Any], pcs: PcpServer) => {
        BIG_STRING3
      })
    )
  )

  test("string with special chars1") {
    val server = PcpRpc.getPCServer(sandbox = sandbox)
    val pool = PcpRpc.getPCClientPool(
      getServerAddress = () => Future { PcpRpc.ServerAddress(port = server.getPort()) },
      generateSandbox = (_) => new Sandbox(Map[String, BoxFun]())
    )

    try {
      val p = new PcpClient()
      Await.result(
        Future.sequence(
          (1 to 1000 map { _ =>
            pool.call(p.call("bigString")) map { result =>
              assert(result == BIG_STRING)
            }
          }).toList
        ),
        30.seconds
      )
    } finally {
      server.close()
    }
  }

  test("string with special chars2") {
    val server = PcpRpc.getPCServer(sandbox = sandbox)
    val pool = PcpRpc.getPCClientPool(
      getServerAddress = () => Future { PcpRpc.ServerAddress(port = server.getPort()) },
      generateSandbox = (_) => new Sandbox(Map[String, BoxFun]())
    )

    try {
      val p = new PcpClient()
      Await.result(Future.sequence((1 to 1000 map { _ =>
        pool.call(p.call("bigString2")) map { result =>
          assert(result == BIG_STRING2)
        }
      }).toList), 30.seconds)
    } finally {
      server.close()
    }
  }

  test("string with special chars3") {
    val server = PcpRpc.getPCServer(sandbox = sandbox)
    val pool = PcpRpc.getPCClientPool(
      getServerAddress = () => Future { PcpRpc.ServerAddress(port = server.getPort()) },
      generateSandbox = (_) => new Sandbox(Map[String, BoxFun]())
    )

    try {
      val p = new PcpClient()
      Await.result(
        Future.sequence(
          (1 to 1000 map { _ =>
            pool.call(p.call("bigString3")) map { result =>
              assert(result == BIG_STRING3)
            }
          }).toList
        ),
        30.seconds
      )
    } finally {
      server.close()
    }
  }
}
