## Description for app team response format

app spec overview on [here](http://10.97.13.150:9000/doc/els_api_spec.html)


## Search API
Operation   
`GET /search`

##### Request example
* 搜尋`玩味丹寧`、`第2頁`、`價格區問1800~3000`、用json pretty print、來源平台為`app`
```
10.97.13.150:9000/search?query=玩味丹寧&page=2&price_lower=1800&price_upper=3000&pretty&platform=App
```

* 搜尋`玩味丹寧`、找`3C` 館底下的商品、`價格區問1800~3000`、來源平台為`app`
```
10.97.13.150:9000/search?query=玩味丹寧&filter=S0:&price_lower=1800&price_upper=3000&platform=App
```



#### Response
* 無資料時為null(含products、aggregations)

Property | Type | Description
--|--|--
`total` | integer | 總筆數
`products` | array of [object](#products) | 搜尋結果
`aggregations` | array of [object](#aggregations) | 各目錄搜尋筆數

 * <a name="products"></a> `products`  

Property | Type | Description
--|--|--
`sid` |  integer | 館ID
`cid` |  integer | 主目錄ID
`pid` |  integer | 商品編號
`spid` | intger | 上架檔ID
`name` |  integer | 品名
`badge` | string | 小標
`img` | string | `prd_related_file.type=3` 圖片url
`price` | integer | 價格，特惠價、促銷價選最小值顯示
`type` | string | 商品類型, `NORMAL: 一般商品`, `PREORDER: 預購商品`, `CART_A: 購物車目錄`, `PROMOTIONAL: 促銷目錄`, `COMBINED: 組合商品`
`restricted` | boolean | 是否為限制級商品
`sold_out` | boolean | 是否賣完


 * <a name="aggregations"></a> `aggregations`  
  * 對搜尋結果(所有商品)做各目錄商品數計算, 不理會request的`filter`、`price_lower`、`price_upper`篩選
  * app format只呈現館的計算數量


 Property | Type | Description
 --|--|--
 `id` |  string | 目錄id
 `name` | string | 目錄名稱
 `count` |  integer | 該分類搜尋結果加總數


##### Response example
```
{
  "response" : {
    "status" : "Ok",
    "message" : "response ok",
    "timestamp" : 1447237106470
  },
  "payload" : {
    "total" : 6409,
    "products" : [ {
      "sid" : 146,
      "cid" : 176735,
      "pid" : 4939040,
      "spid" : 42965830,
      "name" : "【estilo】玩味丹寧系列 恣意隨興 後背包(條紋灰)",
      "badge" : null,
      "img" : "https://img.gohappy.com.tw/images/product/164/4939040/4939040_3_1.jpg?902592",
      "price" : 2680,
      "type" : "NORMAL",
      "restricted" : false,
      "soldout" : false
    }, {
      "sid" : 309,
      "cid" : 185918,
      "pid" : 4413948,
      "spid" : 44187794,
      "name" : "【estilo】玩味丹寧系列 恣意隨興 後背包(藍)",
      "badge" : null,
      "img" : "https://img.gohappy.com.tw/images/product/147/4413948/4413948_3_1.jpg?937673",
      "price" : 2358,
      "type" : "NORMAL",
      "restricted" : false,
      "soldout" : false
    } ],
    "aggregations" : [ {
      "id" : "S406:　",
      "name" : "2.Maa",
      "count" : 2
    }, {
      "id" : "S0:　",
      "name" : "3C",
      "count" : 9
    }, {
      "id" : "S467:　",
      "name" : "A Lady s真皮",
      "count" : 14
    }, {
      "id" : "S370:　",
      "name" : "Arnold Palmer",
      "count" : 1
    }, {
      "id" : "S331:　",
      "name" : "BAG TO YOU",
      "count" : 14
    }, {
      "id" : "S284:　",
      "name" : "BOBSON",
      "count" : 10
    }, {
      "id" : "S335:　",
      "name" : "Bonjour",
      "count" : 6
    }, {
      "id" : "S337:　",
      "name" : "DN",
      "count" : 1
    }, {
      "id" : "S243:　",
      "name" : "EASY SPIRIT",
      "count" : 1
    }, {
      "id" : "S269:　",
      "name" : "EDWIN",
      "count" : 14
    }, {
      "id" : "S593:　",
      "name" : "Eclife 良興購物網",
      "count" : 1
    }, {
      "id" : "S487:　",
      "name" : "LIYO理優",
      "count" : 3
    }, {
      "id" : "S601:　",
      "name" : "MOMA服裝",
      "count" : 2
    }, {
      "id" : "S489:　",
      "name" : "Maya Wind",
      "count" : 20
    }, {
      "id" : "S543:　",
      "name" : "Monsieur 尚先生",
      "count" : 1
    }, {
      "id" : "S341:　",
      "name" : "Outlet品牌特賣匯",
      "count" : 35
    }, {
      "id" : "S468:　",
      "name" : "PBH 日系精品衛浴",
      "count" : 1
    }, {
      "id" : "S399:　",
      "name" : "SOPHIE&SAM",
      "count" : 2
    }, {
      "id" : "S324:　",
      "name" : "SmaLife品牌童裝",
      "count" : 1
    }, {
      "id" : "S309:　",
      "name" : "THINKPINK",
      "count" : 3
    }, {
      "id" : "S200:　",
      "name" : "TOP GIRL",
      "count" : 1
    }, {
      "id" : "S407:　",
      "name" : "UniWeather",
      "count" : 2
    }, {
      "id" : "S485:　",
      "name" : "WOW2",
      "count" : 10
    }, {
      "id" : "S454:　",
      "name" : "WuMi 無米",
      "count" : 2
    }, {
      "id" : "S554:　",
      "name" : "aLovin 婭薇恩",
      "count" : 1
    }, {
      "id" : "S319:　",
      "name" : "cantwo",
      "count" : 2
    }, {
      "id" : "S557:　",
      "name" : "cecile",
      "count" : 8
    }, {
      "id" : "S167:　",
      "name" : "city'super",
      "count" : 14
    }, {
      "id" : "S359:　",
      "name" : "ef-de少淑女",
      "count" : 3
    }, {
      "id" : "S8:　",
      "name" : "傢俱收納",
      "count" : 45
    }, {
      "id" : "S533:　",
      "name" : "台灣鞋網&TOMO",
      "count" : 2
    }, {
      "id" : "S146:　",
      "name" : "太平洋SOGO",
      "count" : 23
    }, {
      "id" : "S275:　",
      "name" : "寢飾床墊",
      "count" : 14
    }, {
      "id" : "S158:　",
      "name" : "居家生活",
      "count" : 13
    }, {
      "id" : "S415:　",
      "name" : "愛的世界",
      "count" : 3
    }, {
      "id" : "S12:　",
      "name" : "愛買線上購物",
      "count" : 36
    }, {
      "id" : "S5:　",
      "name" : "服裝",
      "count" : 63
    }, {
      "id" : "S1:　",
      "name" : "相機手機",
      "count" : 7
    }, {
      "id" : "S602:　",
      "name" : "禮味生活美學",
      "count" : 6
    }, {
      "id" : "S4:　",
      "name" : "精品手錶",
      "count" : 7
    }, {
      "id" : "S3:　",
      "name" : "美妝",
      "count" : 16
    }, {
      "id" : "S9:　",
      "name" : "親子圖書",
      "count" : 65
    }, {
      "id" : "S7:　",
      "name" : "運動休閒",
      "count" : 22
    }, {
      "id" : "S145:　",
      "name" : "遠東百貨",
      "count" : 24
    }, {
      "id" : "S190:　",
      "name" : "鞋包配飾",
      "count" : 94
    }, {
      "id" : "S238:　",
      "name" : "麥雪爾MYVEGA",
      "count" : 1
    } ]
  }
}

```
