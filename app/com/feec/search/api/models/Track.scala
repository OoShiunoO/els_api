package com.feec.search.api.models

import java.util.Date

import com.feec.search.api.common.enum.Platform.Platform

case class SearchTrack(date: Date, ip: String, oriKeyword: String, keyword: String, filter: String, priceLower: Int, priceUpper: Int, page: Int, size: Int, platform: Platform, responseStatus: String, total: Int, took: Long)

object SearchTrack {
  def apply(remoteAddress: String, responseStatus: String, total: Int, took: Long): SearchTrack = SearchTrack(new Date, remoteAddress, null, null, null, 0, 0, 0, 0, null, responseStatus, total, took)
}
