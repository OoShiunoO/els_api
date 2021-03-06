package com.feec.search.api.common.utils

import java.sql.{Connection, DriverManager}

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.typesafe.config.ConfigFactory


object DBUtils {

  /**
   * Init Class for Loader
   */
  def initForClass(driver: String) = Class.forName(driver)

  /**
   * connect to database with uri, user, and passowrd
   */
  def connect(uri: String, user: String, password: String) =
    DriverManager.getConnection(uri, user, password)


  def rollback(conn: Connection) = {
    try {
      conn.rollback()
    } catch {
      case ex: Throwable =>
    }
  }


  /**
   * close AutoCloseable resource in silence
   */
  def close(resource: AutoCloseable) {
    if (resource != null) {
      try {
        resource.close()
      }
      catch {
        case ex: Throwable =>

      }
    }
  }

}

object DCConnectionPool {
  val ecConfig = ConfigFactory.load().getConfig("DB.DC")

  private val pool: ComboPooledDataSource = new ComboPooledDataSource()
  pool.setDriverClass(ecConfig.getString("driver"))
  pool.setJdbcUrl(ecConfig.getString("url"))
  pool.setUser(ecConfig.getString("user"))
  pool.setPassword(ecConfig.getString("password"))
  pool.setMaxPoolSize(15)
  pool.setTestConnectionOnCheckin(true)
  pool.setIdleConnectionTestPeriod(30)

  def getConnection() = {
    val conn = pool.getConnection
    conn.setAutoCommit(false)
    conn
  }

}