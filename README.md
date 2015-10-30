# Elastic Search engine API
build on play framework 2.4 with scala development

#### package
`activator dist`


IP : 10.97.13.150:9000

提供功能
* search

## Api Spec
若GitLab README 破版可讀 [els_api_spec.html](/doc/els_api_spec.html)

## Common
#### Request

Property | Type | Description | Required
--|--|--|--
`pretty`| | JSON pretty print, 可不用key值   |

#### Response
Property | Type | Description
--|--|--
`status_code`| integer  | response狀態代碼
`status_message`| integer  | response狀態訊息
`pretty` | boolean | JSON pretty print
`took` | integer | 花費時間(milliseconds )
`data`| array of object | 查詢結果

## Search API
Operation   
`GET /search`

#### Request
**輸入異常值會以default為主**

Property | Type | Description | Required
--|--|--|--
`query`|string | 搜尋字串   | Y
`page` |  integer | 頁數 `default = 1` |
`size` |  integer | 每頁呈現商品數 `default = 20`|
`filter` |  string | 對搜尋結果的filter,可直接使用`response:aggregations:category_path_id`|
`platform`|  string  | 來源平台, `App: IOS & Android`, `Web: 大網`, `default=Web`  | (還未依各平台做個別呈現)
`sort` | string | 商品排序方式, `Related: 最相關`, `PriceLow: 網路價從低到高`, `PriceHigh: 網路價從高到低`, `default=Related`  |
`price_lower` | integer | 網路價價格區間(低) | 含本身, 小於等於0不處理
`price_upper` | integer | 網路價價格區間(高) | 含本身, 小於等於0不處理



##### Request example
* 搜尋雙肩後背包、第2頁、價格區問1800~3000、商品排序方式用檟格從低到高、用json pretty print
```
10.97.13.150:9000/search?platform=Web&query=雙肩後背包&page=2&price_lower=1800&price_upper=3000&sort=PRICE_LOW&pretty
```

* 搜尋雙肩後背包、找`3C#S0:　>　NB筆電#581　>　品牌筆電包#14242` 目錄底下的商品、價格區問1800~3000
```
10.97.13.150:9000/elsapi/search?platform=Web&query=金牌啤酒&filter=S0:>581:>14242&price_lower=300&price_upper=2000
```


#### Response
Property | Type | Description
--|--|--
`total` | integer | 總筆數
`hits` | array of [object](#hits) | 搜尋結果
`aggregations` | array of [object](#aggregations) | 各目錄搜尋筆數

 * <a name="hits"></a> `hits`  
 **無資料為null**

Property | Type | Description
--|--|--
`product_id` |  integer | 商品編號
`product_name` |  integer | 品名
`desc_brief` | string | 商品簡介
`name_label` | string | 小標
`main_category_id` | integer | 商品主目錄
`picture` | array of [object](#picture) | 相關圖檔路徑   *
`all_category_path` | array of string | all category, ex: 遠東百貨#S145　>　相機手機#35444　>　手機週邊#304300　>　手機保護殼#304612
`all_category_path_id` | array of string | all category id, ex:S145>35444>304300>304612
`sell_set_id` | integer | 銷售組合 *
`combined` | boolean | 是否為組合商品 *
`allowance` | boolean | 是否為結帳再折扣 *
`discount` | boolean | 是否為點我在折扣 *
`restricted` | boolean | 是否為限制級商品 *
`disable` |	boolean |	是否顯示該商品 `default = false` *
`pre_order_type` | string | 商品訂購類型(預購), `NONE: 一般商品`, `PREORDER: 預購商品`, `CUSTOM: 訂製商品`, `DEAL: 客約商品` *
`market_price` | integer | 市價
`member_price`| integer | 網路價
`onsale_price` | integer | 促銷價
`onsale_start_date` | integer | 促銷起始日
`onsale_end_date` | integer | 促銷結束日
`partial_price` | integer | 點+金的金額
`partial_point` | integer | 點+金的點數
`exchange_point` | integer | 純點數兌換
`shelf_id` | intger | 上架檔ID *
`isbn` | intger | ISBN(書類用)
`author` | string | 作者(書類用)
`publisher` | string | 出版社(書類用)


  * <a name="picture"></a> `picture`


 Property | Type | Description
 --|--|--
 `type_id` |  integer | 圖片型態, `1:縮圖(80*80)`, `2:小圖一(160*160)`, `3:大圖(350*350)`
 `picture_name` |  integer | 圖片檔名


 * <a name="aggregations"></a> `aggregations`  
 對搜尋結果(所有商品)做各目錄商品數計算, 不理會`filter`篩選


 Property | Type | Description
 --|--|--
 `category_path_name` |  string | 目錄名稱
 `category_path_id` | string | 目錄id
 `doc_count` |  integer | 該分類搜尋結果加總數

 若parent cid有多個child cid，每個child cid有一樣的商品，則parent的商品數量不會重複計算
 ex:

path name | count
--|--|
3C　>　NB筆電　>　品牌筆電包  |**數量14**  
3C　>　NB筆電　>　品牌筆電包　>　├ Targus 13~14吋 | **數量1**  
3C　>　NB筆電　>　品牌筆電包　>　後背包 | **數量14**

 中目錄`品牌筆電包`的商品數為`14`


##### Response example
```
{
   "status_code": 1
   "took": 63,
   "pretty": true,
   "data": {
     "total": 2,        
     "hits": [
           {
                 "pre_order_type": "NONE",
                 "allowance": false,
                 "name_label": "★結帳9折特賣",
                 "market_price": 3490,
                 "all_category_path": [
                    "3C#S0:　>　NB筆電#581　>　品牌筆電包#14242　>　後背包#592"
                 ],
                 "shelf_id": 36289841,
                 "partial_point": 0,
                 "main_category_id": 592,
                 "product_name": "Thule 都樂EnRoute™ Mosey 多功能 15吋 雙肩後背包 TEMD-115黑色",
                 "member_Price": 3490,
                 "all_category_path_id": [
                    "S0:>581>14242>592"
                 ],
                 "exchange_point": 0,
                 "product_id": 3246896,
                 "picture": [
                    {
                       "type_id": 1,
                       "picture_name": "3246896_1.jpg?556595"
                    },
                    {
                       "type_id": 2,
                       "picture_name": "3246896_2.jpg?960303"
                    },
                    {
                       "type_id": 3,
                       "picture_name": "3246896_3_1.jpg?249808"
                    },
                    {
                       "type_id": 3,
                       "picture_name": "3246896_3_2.jpg?509941"
                    }
                 ],
                 "disable": false,
                 "restricted": false,
                 "sell_set_id": 11,
                 "partial_price": 0,
                 "desc_brief": "\r\n\t● 聚酯纖維材質 \r\n\t● 輕便且耐用 \r\n\t● 可放置15吋MacBook筆電/iPad平板電腦\r\n\t● 28公升掀蓋式設計方便收納\r\n\t● 符合人體功能透氣背墊設計&nbsp;&nbsp;",
                 "combined": false,
                 "discount": false
           },
           {
               "pre_order_type": "NONE",
               "allowance": false,
               "name_label": "★結帳85折特賣",
               "market_price": 4980,
               "all_category_path": [
                  "3C#S0:　>　NB筆電#581　>　品牌筆電包#14242　>　後背包#592",
                  "3C#S0:　>　NB筆電#581　>　品牌筆電包#14242　>　Incase 筆電包#35623",
                  "遠東百貨#S145:　>　鞋包配飾#300909　>　運動休閒包#304236　>　Incase 筆電包#224035",
                  "鞋包配飾#S190:　>　運動休閒包#22785　>　電腦/商務#29684　>　更多商務包 more..#177817"
               ],
               "shelf_id": 36279447,
               "partial_point": 0,
               "main_category_id": 592,
               "product_name": "Incase City 城市系列 17 吋雙層後背包-卡其色",
               "member_Price": 4980,
               "all_category_path_id": [
                  "S0:>581>14242>592",
                  "S0:>581>14242>35623",
                  "S145:>300909>304236>224035",
                  "S190:>22785>29684>177817"
               ],
               "exchange_point": 0,
               "product_id": 3661961,
               "picture": [
                  {
                     "type_id": 1,
                     "picture_name": "3661961_1.jpg?123527"
                  },
                  {
                     "type_id": 2,
                     "picture_name": "3661961_2.jpg?267400"
                  },
                  {
                     "type_id": 3,
                     "picture_name": "3661961_3_6.jpg?308852"
                  },
                  {
                     "type_id": 3,
                     "picture_name": "3661961_3_1.jpg?278601"
                  },
                  {
                     "type_id": 3,
                     "picture_name": "3661961_3_2.jpg?901032"
                  },
                  {
                     "type_id": 3,
                     "picture_name": "3661961_3_3.jpg?848170"
                  },
                  {
                     "type_id": 3,
                     "picture_name": "3661961_3_4.jpg?391805"
                  },
                  {
                     "type_id": 3,
                     "picture_name": "3661961_3_5.jpg?118616"
                  }
               ],
               "disable": false,
               "restricted": false,
               "sell_set_id": 11,
               "partial_price": 0,
               "desc_brief": "\r\n\t&nbsp;採用耐用 270x500D 混合編織聚脂纖維材質\r\n\t堅固 900D 三層表布塗層\r\n\t絨毛內襯與 360 度襯墊\r\n\t內部超大收納空間\r\n\t&nbsp;",
               "combined": false,
               "discount": false
           }
        ],
           "aggregations": [
              {
                 "category_path_name": "3C　",
                 "category_path_id": "S0",
                 "doc_count": 14
              },
              {
                 "category_path_name": "3C　>　NB筆電　",
                 "category_path_id": "S0>581",
                 "doc_count": 14
              },
              {
                 "category_path_name": "3C　>　NB筆電　>　品牌筆電包　",
                 "category_path_id": "S0>581>14242",
                 "doc_count": 14
              },
              {
                 "category_path_name": "3C　>　NB筆電　>　品牌筆電包　>　Incase 筆電包",
                 "category_path_id": "S0>581>14242>35623",
                 "doc_count": 2
              },
              {
                 "category_path_name": "3C　>　NB筆電　>　品牌筆電包　>　Incase 時尚包",
                 "category_path_id": "S0>581>14242>27477",
                 "doc_count": 1
              },
              {                 
                 "category_path_name": "3C　>　NB筆電　>　品牌筆電包　>　└ Targus 15吋↗",
                 "category_path_id": "S0>581>14242>21945",
                 "doc_count": 4
              },
              {
                 "category_path_name": "3C　>　NB筆電　>　品牌筆電包　>　├ Targus 13~14吋",
                 "category_path_id": "S0>581>14242>76596",
                 "doc_count": 1
              },
              {
                 "category_path_name": "3C　>　NB筆電　>　品牌筆電包　>　後背包",
                 "category_path_id": "S0>581>14242>592",
                 "doc_count": 14
              }
           ]
         }
}

```
