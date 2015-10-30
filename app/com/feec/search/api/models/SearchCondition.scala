package com.feec.search.api.models

import com.feec.search.api.common.enum.Platform.Platform

case class OriSearchCondition(oriQueryString: Option[String], page: Option[String], size: Option[String], filter: Option[String], platform: Option[String], sort: Option[String], priceLower: Option[String], priceUpper: Option[String])

case class SearchCondition(oriQueryString: Option[String], queryString: String, page: Int, size: Int, filters: List[ElsFilter], platform: Platform, sort: List[ElsSort])
