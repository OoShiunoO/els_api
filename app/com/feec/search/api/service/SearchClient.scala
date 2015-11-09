package com.feec.search.api.service


import com.feec.search.api.common.enum.Platform
import com.feec.search.api.common.enum.Platform._
import com.feec.search.api.common.utils.TransformUtils
import com.feec.search.api.models.SearchCondition
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._


object QueryClient {
  val scoreSort = field sort "_score"
  val picDomain = "https://img.gohappy.com.tw/images/product"

  def query(condition: SearchCondition) = {
    val client = ElsClient.connectEls
    val typeName = ElsClient.typeName
    val size = condition.size
    val startNum = ((condition.page - 1) * size)

    val queryString = condition.queryString

    val result = client.execute {
      val queryDefine = search in "product" / typeName start startNum limit size rawQuery {
        s"""{
           |  "bool": {
           |            "should": [
           |                { "match": { "product_name":
           |                    {"query":"$queryString" ,
           |                    "operator" : "or",
           |                      "boost" : 10
           |                    }
           |                  }
           |                },
           |                { "match": { "desc_brief":
           |                    {"query":"$queryString" ,
           |                    "operator" : "or",
           |                      "boost" : 5
           |                    }
           |                  }
           |                },
           |                { "match": { "name_label":
           |                    {"query":"$queryString" ,
           |                    "operator" : "or",
           |                      "boost" : 4
           |                    }
           |                  }
           |                },
           |                { "match": { "category_path":
           |                    {"query":"$queryString" ,
           |                    "operator" : "or",
           |                      "boost" : 1
           |                    }
           |                  }
           |                }
           |            ]
           |        }
           |
           |  }
           | """.stripMargin
      } aggs {
        aggregation terms "category_aggs" field "all_category_path" size 0 order Terms.Order.aggregation("_term", true)
      }

      val filters = condition.filters.map {
        ElsColumnDefine.defineFilter
      }

      if (!filters.isEmpty) {
        queryDefine postFilter {
          and(filters: _*)
        }
      }


      val sortList = condition.sort.map { elsSort => field sort elsSort.key order elsSort.order } :+ scoreSort

      queryDefine sort (sortList: _*)

      println(s"query json : $queryDefine")
      queryDefine
    }.await

    client.close()
    result
  }

  def extractSearchResponse(resp: SearchResponse, platform: Platform) = {
    val json = Json.parse(resp.toString)

    val elsToClean = platform match {
      case Platform.Web =>
        (__ \ 'data).json.copyFrom(
          (__ \ 'total).json.copyFrom((__ \ 'hits \ 'total).json.pick) and
            (__ \ 'hits).json.copyFrom((__ \ 'hits \ 'hits).json.pick(
              of[JsArray].map { case JsArray(arr) => JsArray(arr.map {
                _.transform((__ \ '_source).json.pick).get
              })
              }
            )) and
            (__ \ 'aggregations).json.copyFrom(
              (__ \ 'aggregations \ 'category_aggs \ 'buckets).json.pick(
                of[JsArray].map { case JsArray(arr) => JsArray(arr.map {
                  agg =>
                    val key = agg.transform((__ \ 'key).json.pick).get.as[String]
                    val category = splitCategory(key)
                    agg.transform((__ \ 'category_path_name).json.put(JsString(category._1)) and
                      (__ \ 'category_path_id).json.put(JsString(category._2)) and
                      (__ \ 'doc_count).json.pickBranch
                      reduce
                    ).get
                })
                }
              )
            )
            reduce)
      case Platform.App =>
        (__ \ 'data).json.copyFrom(
          (__ \ 'total).json.copyFrom((__ \ 'hits \ 'total).json.pick) and
            (__ \ 'products).json.copyFrom((__ \ 'hits \ 'hits).json.pick(
              of[JsArray].map { case JsArray(arr) => JsArray(arr.map {
                _.transform((__ \ '_source).json.pick).get
              }.map {
                obj =>
                  val mainCategory = obj.transform((__ \ 'main_category_id).json.pick).get.as[Int]
                  val allCategoryPathId = obj.transform((__ \ 'all_category_path_id).json.pick).get.as[Seq[String]]
                  val tmp = allCategoryPathId.filter {
                    _.contains(mainCategory.toString)
                  }.head
                  val sid = TransformUtils.parseInt(tmp.substring(1, tmp.indexOf(":"))).getOrElse(-1)

                  val pictureUrl = picDomain + obj.transform((__ \ 'picture).json.pick(of[JsArray].map { case JsArray(arr) => JsArray(arr.filter {
                    _.transform((__ \ 'type_id).json.pick).get.as[Int] == 3
                  }.map(_.transform((__ \ 'picture_url).json.pick).get))
                  })).get.as[Seq[String]].head

                  val memberPrice = obj.transform((__ \ 'member_price).json.pick).get
                  val onsalePrice = obj.transform((__ \ 'onsale_price).json.pick).get

                  val correctPrice = onsalePrice match {
                    case JsNull => memberPrice
                    case _ => onsalePrice
                  }


                  obj.transform(
                    (__ \ 'sid).json.put(JsNumber(sid)) and
                      (__ \ 'cid).json.copyFrom((__ \ 'main_category_id).json.pick) and
                      (__ \ 'pid).json.copyFrom((__ \ 'product_id).json.pick) and
                      (__ \ 'spid).json.copyFrom((__ \ 'shelf_id).json.pick) and
                      (__ \ 'name).json.copyFrom((__ \ 'product_name).json.pick) and
                      (__ \ 'badge).json.copyFrom((__ \ 'name_label).json.pick) and
                      (__ \ 'img).json.put(JsString(pictureUrl)) and
                      (__ \ 'price).json.put(correctPrice.as[JsNumber]) and
                      (__ \ 'type).json.copyFrom((__ \ 'product_type).json.pick) and
                      (__ \ 'restricted).json.copyFrom((__ \ 'restricted).json.pick) and
                      (__ \ 'soldout).json.copyFrom((__ \ 'sold_out).json.pick)
                      reduce

                  ).get
              })
              })) and
            (__ \ 'filter).json.copyFrom(
              (__ \ 'aggregations \ 'category_aggs \ 'buckets).json.pick(
                of[JsArray].map { case JsArray(arr) => JsArray(arr.filter {
                  _.transform((__ \ 'key).json.pick).get.as[String].split("　>　").length == 1
                }.map {
                  agg =>
                    val key = agg.transform((__ \ 'key).json.pick).get.as[String]
                    val category = splitCategory(key)
                    agg.transform((__ \ 'id).json.put(JsString(category._2)) and
                      (__ \ 'name).json.put(JsString(category._1)) and
                      (__ \ 'count).json.copyFrom((__ \ 'doc_count).json.pick)
                      reduce
                    ).get
                  //                    Json.obj("category_path_name" -> JsString(category._1), "category_path_id" -> JsString(category._2), "doc_count" -> agg.transform((__ \ 'doc_count).json.pick).get)
                })
                }
              )

            )

            reduce
        )
    }

    val result = json.transform(elsToClean)

    result
  }

  def splitCategory(path: String) = {
    val split = path.split("　>　").map { a =>
      val iar = a.split("#")
      (iar(0), iar(1))
    }

    (split.map(_._1).mkString("　>　"), split.map(_._2).mkString(">"))
  }
}

