package com.feec.search.api.service

import java.util.Date

import com.feec.search.api.common.utils.{DateUtils, DBUtils, DCConnectionPool}
import com.feec.search.api.dao.TrackDao
import com.feec.search.api.models._
import org.joda.time.{Duration, DateTime}
import play.api.libs.json.Reads._
import play.api.libs.json.{Json, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

object TrackService {


  def searchDataCollection(condition: Option[SearchCondition], remoteAddress: String, jsonString: String) = {
    Future {
      try {
        val json = Json.parse(jsonString)
        val responseStatus = json.transform((__ \ 'response \ 'status).json.pick).get.as[String]
        val total = json.transform((__ \ 'payload \ 'total).json.pick).get.as[Int]
        val receiveTimeString = json.transform((__ \ 'response \ 'timestamp).json.pick).get.as[String]

        val receiveTime = DateUtils.TIMESTAMP_FORMATTER.parseDateTime(receiveTimeString)
        val currentTime = new DateTime()
        val duration = new Duration(receiveTime, currentTime)


        val searchTrack = condition match {
          case Some(c) =>
            val prefilter = c.filters.map { case filter: ElsPrefixFilter => filter.filterString
            case _ => ""
            }.find(_.length > 0)

            val lower = c.filters.map { case els: ElsRangeFilter => els.lower.getOrElse(0)
            case _ => 0
            }.find(_ > 0)

            val upper = c.filters.map { case els: ElsRangeFilter => els.upper.getOrElse(0)
            case _ => 0
            }.find(_ > 0)

            SearchTrack(new Date, remoteAddress, c.oriQueryString.getOrElse(null),
              c.queryString, prefilter.getOrElse(null), lower.getOrElse(0),
              upper.getOrElse(0), c.page, c.size, c.platform, responseStatus,
              total, duration.getMillis)
          case None =>
            SearchTrack(remoteAddress, responseStatus, total, duration.getMillis)
        }

        insertSearchTrack(searchTrack)
      } catch {
        case NonFatal(e) => e.printStackTrace
      }
    }
  }

  def insertSearchTrack(searchTrack: SearchTrack) = {
    lazy val conn = DCConnectionPool.getConnection()
    try {
      val result = TrackDao.insertSearchTrack(searchTrack, conn)
      if (result.get > 0)
        conn.commit()

      result
    } catch {
      case NonFatal(ex) =>
        ex.printStackTrace()
        DBUtils.rollback(conn)
    } finally {
      DBUtils.close(conn)
    }
  }

}
