package com.feec.search.api.controllers

import com.feec.search.api.common.enum.ReturnCode
import com.feec.search.api.common.utils.JsonUtils
import com.feec.search.api.models.OriSearchCondition
import com.feec.search.api.service.{ApiService, JsonService, QueryClient}
import play.api.mvc._

import scala.util.control.NonFatal

class SearchController extends Controller {

  def query = Action { request =>

    val current = System.currentTimeMillis()

    val condition = ApiService.checkSearchCondition(OriSearchCondition(request.getQueryString("query"), request.getQueryString("page"), request.getQueryString("size"), request.getQueryString("filter"), request.getQueryString("platform"), request.getQueryString("sort"), request.getQueryString("price_lower"), request.getQueryString("price_upper")))

    lazy val searchResponse = QueryClient.query(condition.get)
    lazy val extractJsonResult = QueryClient.extractSearchResponse(searchResponse, condition.get.platform)

    val code = try {
      condition match {
        case Some(_) =>
          ApiService.checkSearchJsonResult(extractJsonResult, current)
        case None =>
          ReturnCode.LostNecessary
      }
    } catch {
      case NonFatal(e) =>
        e.printStackTrace()
        ReturnCode.InternalException
    }

    val jsonObj = code match {
      case ReturnCode.RespOk => extractJsonResult.get
      case ReturnCode.Empty => extractJsonResult.get
      case ReturnCode.JsonParseError => JsonService.emptySearchResponse
      case ReturnCode.LostNecessary => JsonService.emptySearchResponse
      case ReturnCode.InternalException => JsonService.emptySearchResponse
    }


    val finalJsonObj = JsonService.addHeader(jsonObj, code, System.currentTimeMillis() - current)

    val pretty = request.getQueryString("pretty")

    val finalJsonString = JsonUtils.pretty(finalJsonObj, pretty)

    Ok(finalJsonString).as(JSON)

  }

}
