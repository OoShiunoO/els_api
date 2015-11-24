package com.feec.search.api.dao

import java.sql.{Connection, PreparedStatement}

import com.feec.search.api.common.utils.DBUtils

import scala.util.control.NonFatal


object Insert {
  def insert(conn: Connection)(injectParameter: Connection => PreparedStatement): Option[Int] = {
    lazy val ps = injectParameter(conn)

    try {
      Option(ps.executeUpdate())

    } catch {
      case NonFatal(ex) =>
        throw ex
    } finally {
      DBUtils.close(ps)
    }
  }
}
