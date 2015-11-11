package com.feec.search.api.controllers

import com.feec.search.api.common.enum.Response
import com.feec.search.api.common.utils.JsonUtils
import com.feec.search.api.models.OriSearchCondition
import com.feec.search.api.service.{ApiService, JsonService, QueryClient}
import play.api.mvc._

import scala.util.control.NonFatal

class SearchController extends Controller {

  def query = Action { request =>

    val receiveTime = System.currentTimeMillis()

    val condition = ApiService.checkSearchCondition(OriSearchCondition(request.getQueryString("query"), request.getQueryString("page"), request.getQueryString("size"), request.getQueryString("filter"), request.getQueryString("platform"), request.getQueryString("sort"), request.getQueryString("price_lower"), request.getQueryString("price_upper")))

    lazy val searchResponse = QueryClient.query(condition.get)
    lazy val extractJsonResult = QueryClient.extractSearchResponse(searchResponse, condition.get.platform)

    val status = try {
      condition match {
        case Some(_) =>
          ApiService.checkSearchJsonResult(extractJsonResult)
        case None =>
          Response.LostNecessary
      }
    } catch {
      case NonFatal(e) =>
        e.printStackTrace()
        Response.Error
    }

    val jsonObj = status match {
      case Response.Ok => extractJsonResult.get
      case Response.NoMoreResults => JsonService.cleanAggregations(extractJsonResult.get)
      case Response.Empty => JsonService.emptySearchResponse
      case Response.JsonParseError => JsonService.emptySearchResponse
      case Response.LostNecessary => JsonService.emptySearchResponse
      case Response.Error => JsonService.emptySearchResponse
    }


    val finalJsonObj = JsonService.addHeader(jsonObj, status, receiveTime)

    val pretty = request.getQueryString("pretty")

    val finalJsonString = JsonUtils.pretty(finalJsonObj, pretty)

    Ok(finalJsonString).as(JSON)

  }

}
