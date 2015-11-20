package com.feec.search.api.common.enum

object SynonymsType extends Enumeration {
  type SynonymsType = Value
  val OneWay, TwoWay = Value

  def valueOf(value: Int) = value match {
    case 1 => OneWay
    case 2 => TwoWay
    case _ => throw new Exception
  }
}
