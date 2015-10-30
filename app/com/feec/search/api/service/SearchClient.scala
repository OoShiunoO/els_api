package com.feec.search.api.service


import com.feec.search.api.models.SearchCondition
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._


object QueryClient {
  val scoreSort = field sort "_score"

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


  def extractSearchResponse(resp: SearchResponse) = {
    val json = Json.parse(resp.toString)

    val elsToClean =
    //      (__ \ 'status_code).json.put(JsNumber(code.statusCode)) and
    //      (__ \ 'took).json.pickBranch and
    //      (__ \ 'pretty).json.put(JsBoolean(pretty)) and
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
                  Json.obj("category_path_name" -> JsString(category._1), "category_path_id" -> JsString(category._2), "doc_count" -> agg.transform((__ \ 'doc_count).json.pick).get)
              })
              }
            )
          )
          reduce)
    //    reduce

    val result = json.transform(elsToClean)

    result
  }

  def splitCategory(path: String) = {
    val split = path.split("　>　").map { a =>
      val iar = a.split("#")
      (iar(0), iar(1))
    }

    (split.map(_._1).mkString(" 　>　"), split.map(_._2).mkString(">"))
  }
}

