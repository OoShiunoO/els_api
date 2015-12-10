package com.feec.search.api.controllers

import com.feec.search.api.common.enum.Response
import com.feec.search.api.common.utils.{DateUtils, JsonUtils}
import com.feec.search.api.models.OriSearchCondition
import com.feec.search.api.service.{ApiService, JsonService, SearchClient, TrackService}
import com.google.inject.Inject
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext.Implicits.global

class SearchController @Inject() (dbConfigProvider: DatabaseConfigProvider) extends Controller {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def query = Action.async { request =>

    val receiveTime = new DateTime()

    val condition = ApiService.checkSearchCondition(OriSearchCondition(request.getQueryString("query"), request.getQueryString("page"), request.getQueryString("size"), request.getQueryString("filter"), request.getQueryString("platform"), request.getQueryString("sort"), request.getQueryString("price_lower"), request.getQueryString("price_upper")))

    val status = try {
      condition match {
        case Some(_) => {
          val searchResponse = SearchClient.query(condition.get, dbConfig)
          searchResponse.map { aa =>
            val extractJsonResult = SearchClient.extractSearchResponse(aa, condition.get.platform)
            (Some(extractJsonResult), ApiService.checkSearchJsonResult(extractJsonResult))
          }

        }
        case None =>
          Future.successful((None, Response.LostNecessary))
      }
    } catch {
      case NonFatal(e) =>
        e.printStackTrace()
        Future.successful((None, Response.Error))
    }

    status.map{ bb =>
      val jsonObj = bb._2 match {
        case Response.Ok => bb._1.get.get
        case Response.NoMoreResults => JsonService.cleanAggregations(bb._1.get.get)
        case Response.Empty => JsonService.emptySearchResponse
        case Response.JsonParseError => JsonService.emptySearchResponse
        case Response.LostNecessary => JsonService.emptySearchResponse
        case Response.Error => JsonService.emptySearchResponse
      }


      val finalJsonObj = JsonService.addHeader(jsonObj, bb._2, DateUtils.TIMESTAMP_FORMATTER.print(receiveTime))

      val pretty = request.getQueryString("pretty")

      val finalJsonString = JsonUtils.pretty(finalJsonObj, pretty)

      //data collection, use Future
      TrackService.searchDataCollection(condition, request.remoteAddress, finalJsonString)
      Ok(finalJsonString).as(JSON)
    }







  }

}
