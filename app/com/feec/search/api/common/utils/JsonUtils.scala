package com.feec.search.api.common.utils

import play.api.libs.json.{JsObject, _}


object JsonUtils {
  def pretty(json: JsObject, pretty: Option[String]) = {
    pretty match {
      case Some(_) => Json.prettyPrint(Json.obj("pretty" -> JsBoolean(true)) ++ json)
      case None => (json ++ Json.obj("pretty" -> JsBoolean(false))).toString
    }
  }
}


