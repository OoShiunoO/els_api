package com.feec.search.api.common.enum

object ReturnCode {

  sealed abstract class Code(val statusCode: Int, val message: String)

  case object LostNecessary extends Code(100, "lost necessary parameter")

  case object RespOk extends Code(200, "response ok")

  case object NoMoreResult extends Code(300, "no more result")

  case object Empty extends Code(400, "empty search result")

  case object InternalException extends Code(500, "internal exception")

  case object JsonParseError extends Code(600, "json parse error")


}


object Platform extends Enumeration {
  type Platform = Value
  val Web, App = Value
}

object Sort extends Enumeration {
  type Sort = Value
  val Related, PriceLow, PriceHigh = Value
}