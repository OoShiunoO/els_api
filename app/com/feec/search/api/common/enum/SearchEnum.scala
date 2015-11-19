package com.feec.search.api.common.enum

object Platform extends Enumeration {
  type Platform = Value
  val Web, App, Debug = Value
}

object Sort extends Enumeration {
  type Sort = Value
  val Related, PriceLow, PriceHigh = Value
}