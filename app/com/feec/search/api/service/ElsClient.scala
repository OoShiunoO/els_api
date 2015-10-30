package com.feec.search.api.service

import java.util.Date

import com.feec.search.api.common.utils.DateUtils
import com.feec.search.api.models.{ElsFilter, ElsPrefixFilter, ElsRangeFilter}
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._

object ElsClient {
  var currentElsClient: ElsClient = _

  def connectEls = ElasticClient.remote("localhost", 9300)

  //  def connectEls = ElasticClient.remote("192.168.50.125", 9300)

  def typeName = {
    if (currentElsClient == null || (System.currentTimeMillis > currentElsClient.dayLastSeconds)) {
      currentElsClient = initElsClient
    }
    currentElsClient.currentTypeName
  }


  def initElsClient = {
    val today = new Date
    new ElsClient(s"index_${DateUtils.formatDate(today).get}", DateUtils.lastSeconds(today))
  }

}

class ElsClient(val currentTypeName: String, val dayLastSeconds: Long)

object ElsColumnDefine {
  def defineFilter(filter: ElsFilter) = filter match {
    case ElsPrefixFilter(key, value) => prefixFilter(key, value)

    case ElsRangeFilter(key, lower, upper) =>
      val filter = rangeFilter(key)

      if (lower.isDefined)
        filter gte lower.get.toString
      if (upper.isDefined)
        filter lte upper.get.toString

      filter
  }

  def generateFilters(filters: Option[ElsFilter]*) = {
    for (filter <- filters; if filter.isDefined) yield {
      filter.get
    }
  }


}
