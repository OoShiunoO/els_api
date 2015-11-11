package com.feec.search.api.service

import com.feec.search.api.common.enum._
import com.feec.search.api.common.utils.TransformUtils
import com.feec.search.api.models._
import org.elasticsearch.search.sort.SortOrder
import play.api.libs.json.Reads._
import play.api.libs.json.{JsError, JsObject, JsResult, JsSuccess, _}

import scala.util.Try


object ApiService {
  val antiHTML = """<[^>]*>""".r

  def checkSearchJsonResult(jsonResult: JsResult[JsObject]) = jsonResult match {
    case JsSuccess(jsonObj, jsPath) =>
      val total = jsonObj.transform((__ \ 'payload \ 'total).json.pick).get.as[Int]
      val products = jsonObj.transform((__ \ 'payload \ 'products).json.pick).get.as[Seq[JsObject]]

      if (products.size > 0 && total > 0)
        Response.Ok
      else if (total > 0)
        Response.NoMoreResults
      else
        Response.Empty

    case JsError(e) =>
      println("Errors: " + JsError.toJson(e).toString())
      Response.JsonParseError
  }

  def checkSearchCondition(oric: OriSearchCondition) = {
    if (!oric.oriQueryString.isEmpty) {
      val queryString = antiHTML.replaceAllIn(oric.oriQueryString.get, "").replace(" ", "　")

      val platform = oric.platform match {
        case Some(s) => Try {
          Platform.withName(s)
        }.getOrElse(Platform.Web)
        case None => Platform.Web
      }


      val sort = oric.sort match {
        case Some(s) =>
          Try {
            Sort.withName(s)
          }.getOrElse(Sort.Related)
        case None => Sort.Related
      }

      val sortList = sort match {
        case Sort.PriceHigh => List(ElsSort("market_price", SortOrder.DESC))
        case Sort.PriceLow => List(ElsSort("market_price", SortOrder.ASC))
        case Sort.Related => Nil
      }


      val page = TransformUtils.parseInt(oric.page) match {
        case Some(p) => if (p <= 0) 1 else p
        case None => 1
      }
      val size = TransformUtils.parseInt(oric.size) match {
        case Some(s) => if (s <= 0) 20 else s
        case None => 20
      }


      val priceLower = TransformUtils.parseInt(oric.priceLower) match {
        case Some(p) => if (p <= 0) None else Option(p)
        case None => None
      }

      val priceUpper = TransformUtils.parseInt(oric.priceUpper) match {
        case Some(p) => if (p <= 0) None else Option(p)
        case None => None
      }

      val rangeFilter = (priceLower, priceUpper) match {
        case (None, None) => None
        case _ => Option(ElsRangeFilter("market_price", priceLower, priceUpper))
      }

      val prefixFilter = oric.filter match {
        case Some(filter) => Option(ElsPrefixFilter("all_category_path_id", filter))
        case None => None
      }

      val filters = ElsColumnDefine.generateFilters(prefixFilter, rangeFilter).toList

      Option(SearchCondition(oric.oriQueryString, queryString, page, size, filters, platform, sortList))
    }
    else
      None
  }
}




