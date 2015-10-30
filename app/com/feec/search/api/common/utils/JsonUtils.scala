package com.feec.search.api.common.utils

import play.api.libs.json.{JsObject, _}


object JsonUtils {

  def pretty(json: JsObject, pretty: Option[String]) = {
    pretty match {
      case Some(_) => Json.prettyPrint(Json.obj("pretty" -> JsBoolean(true)) ++ json)
      case None => (json ++ Json.obj("pretty" -> JsBoolean(false))).toString
    }
  }

  def addSpendSec(json: JsObject, time: Long) = {
    Json.obj("took" -> JsNumber(System.currentTimeMillis() - time)) ++ json
  }
}

object JsonTemplate {
  def emptySearchResponse = {
    println("call empty")
    Json.obj().transform(
      (__ \ 'data).json.put(Json.obj("total" -> JsNumber(0), "hits" -> Json.arr(), "aggregations" -> Json.arr()))).get
  }
}
