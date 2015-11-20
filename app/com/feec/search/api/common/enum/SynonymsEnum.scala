package com.feec.search.api.common.enum

object SynonymsType extends Enumeration {
  type SynonymsType = Value
  val Synonyms, Related = Value

  def valueOf(query: String) = query match {
    case "SYN" => Synonyms
    case "REL" => Related
    case _ => throw new Exception
  }
}
