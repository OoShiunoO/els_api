package com.feec.search.api.common.utils

import scala.util.Try

object TransformUtils {


  def parseInt(s: Option[String]) = s match {
    case Some(str) => Try {
      s.get.toInt
    } toOption
    case None => None
  }
}
