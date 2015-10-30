package com.feec.search.api.models

import org.elasticsearch.search.sort.SortOrder


sealed trait ElsFilter

case class ElsPrefixFilter(key: String, filter: String) extends ElsFilter

case class ElsRangeFilter(key: String, lower: Option[Int], upper: Option[Int]) extends ElsFilter


case class ElsSort(key: String, order: SortOrder)