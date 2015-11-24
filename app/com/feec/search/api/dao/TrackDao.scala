package com.feec.search.api.dao

import java.sql.Connection

import com.feec.search.api.models.SearchTrack


object TrackDao {
  val searchTrackSql = "insert into search_track values (?,?,?,?,?,?,?,?,?,?,?,?,?)"

  def insertSearchTrack(searchTrack: SearchTrack, conn: Connection) = {
    Insert.insert(conn) { conn =>

      val ps = conn.prepareStatement(searchTrackSql)
      ps.setTimestamp(1, new java.sql.Timestamp(searchTrack.date.getTime))
      ps.setString(2, searchTrack.ip)
      ps.setString(3, searchTrack.oriKeyword)
      ps.setString(4, searchTrack.keyword)
      ps.setString(5, searchTrack.filter)
      ps.setInt(6, searchTrack.priceLower)
      ps.setInt(7, searchTrack.priceUpper)
      ps.setInt(8, searchTrack.page)
      ps.setInt(9, searchTrack.size)
      ps.setString(10, searchTrack.platform.toString)
      ps.setString(11, searchTrack.responseStatus)
      ps.setInt(12, searchTrack.total)
      ps.setLong(13, searchTrack.took)
      ps
    }
  }

}