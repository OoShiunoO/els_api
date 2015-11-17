package com.feec.search.api.common.enum

object Response {

  sealed abstract class Status(val code: Int, val message: String)

  case object LostNecessary extends Status(100, "lost necessary parameter")

  case object Ok extends Status(200, "response ok")

  case object NoMoreResults extends Status(300, "no more results")

  case object Empty extends Status(400, "empty search result")

  case object Error extends Status(500, "internal exception")

  case object JsonParseError extends Status(600, "json parse error")

}


object Platform extends Enumeration {
  type Platform = Value
  val Web, App, Debug = Value
}

object Sort extends Enumeration {
  type Sort = Value
  val Related, PriceLow, PriceHigh = Value
}