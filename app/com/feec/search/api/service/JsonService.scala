package com.feec.search.api.service

import com.feec.search.api.common.enum.Response
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object JsonService {
  val picDomain = "https://img.gohappy.com.tw/images/product"

  val emptySearchResponse = Json.obj().transform(
    (__ \ 'payload).json.put(Json.obj("total" -> JsNumber(0), "products" -> JsNull, "aggregations" -> JsNull))).get

  def addHeader(jsonObj: JsObject, status: Response.Status, timestamp: String) = {
    Json.obj("response" -> Json.obj("status" -> status.toString, "message" -> status.message, "timestamp" -> timestamp)) ++ jsonObj
  }


  def cleanAggregations(jsonObj: JsObject) = {
    jsonObj.transform((__ \ 'payload).json.pickBranch(
      (__ \ 'total).json.pickBranch and
        (__ \ 'products).json.put(JsNull) and
        (__ \ 'aggregations).json.put(JsNull)
        reduce
    )).get
  }

}

