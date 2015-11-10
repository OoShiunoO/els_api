package com.feec.search.api.service

import com.feec.search.api.common.enum._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object JsonService {
  val picDomain = "https://img.gohappy.com.tw/images/product"

  val emptySearchResponse = Json.obj().transform(
    (__ \ 'data).json.put(Json.obj("total" -> JsNumber(0), "products" -> JsNull, "aggregations" -> JsNull))).get

  def addHeader(jsonObj: JsObject, code: ReturnCode.Code, took: Long) = {
    Json.obj("status_code" -> code.statusCode, "status_message" -> code.message, "took" -> took) ++ jsonObj
  }


  def cleanAggregations(jsonObj: JsObject) = {
    jsonObj.transform((__ \ 'data).json.pickBranch(
      (__ \ 'total).json.pickBranch and
        (__ \ 'products).json.put(JsNull) and
        (__ \ 'aggregations).json.put(JsNull)
        reduce
    )).get
  }


}

