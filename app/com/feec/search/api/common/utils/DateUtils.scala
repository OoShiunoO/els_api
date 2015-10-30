package com.feec.search.api.common.utils

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import scala.util.Try

object DateUtils {

  val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
  val DATE_FORMAT = "yyyyMMdd"


  def parse(value: String, pattern: String): Option[Date] = Try {
    val sdf = new SimpleDateFormat(pattern)
    sdf.parse(value)
  } toOption


  def parseDate(value: String, pattern: String = DATE_FORMAT) = parse(value, pattern)

  def parseDateTime(value: String, pattern: String = DATE_TIME_FORMAT) = parse(value, pattern)


  def format(date: Date, pattern: String): Option[String] = Try {
    val sdf = new SimpleDateFormat(pattern)
    sdf.format(date)
  } toOption


  def formatDate(date: Date, pattern: String = DATE_FORMAT) = format(date, pattern)

  def formatDateTime(date: Date, pattern: String = DATE_TIME_FORMAT) = format(date, pattern)

  def formatOptionDateTime(date: Option[Date], pattern: String = DATE_TIME_FORMAT) = if (date.isEmpty) None else format(date.get, pattern)

  def lastSeconds(date: Date) = {
    val c = Calendar.getInstance()
    c.setTime(date)
    c.set(Calendar.HOUR, 23)
    c.set(Calendar.MINUTE, 59)
    c.set(Calendar.SECOND, 59)
    c.getTimeInMillis
  }

}