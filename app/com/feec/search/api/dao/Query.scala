package com.feec.search.api.dao

import java.sql.{ResultSet, PreparedStatement, Connection}


import com.feec.search.api.common.utils.DBUtils

import scala.util.control.NonFatal


object Query {

  def storeToList[A](getConnection: () => Connection)(injectParameter: Connection => PreparedStatement)(storeResult: ResultSet => A)(closeConnection: Boolean = true): Option[List[A]] = {
    lazy val conn = getConnection()
    lazy val ps = injectParameter(conn)
    lazy val rs = ps.executeQuery()
    val result = scala.collection.mutable.ArrayBuffer.empty[A]

    try {
      while (rs.next) {
        result += storeResult(rs)
      }

      if (result.size > 0)
        Option(result.toList)
      else
        None

    } catch {
      case NonFatal(ex) =>
        throw ex
    } finally {
      DBUtils.close(rs)
      DBUtils.close(ps)
      if (closeConnection)
        DBUtils.close(conn)
    }
  }

  def storeToSingle[A](getConnection :()=> Connection)(injectParameter: Connection => PreparedStatement)(storeResult: ResultSet => A)(closeConnection:Boolean = true): Option[A] = {
    lazy val conn = getConnection()
    lazy val ps = injectParameter(conn)
    lazy val rs = ps.executeQuery()

    try {
      if(rs.next) {
        Option(storeResult(rs))
      } else
        None

    } catch {
      case NonFatal(ex) =>
        throw ex
    } finally {
      DBUtils.close(rs)
      DBUtils.close(ps)
      if(closeConnection)
        DBUtils.close(conn)
    }
  }
}