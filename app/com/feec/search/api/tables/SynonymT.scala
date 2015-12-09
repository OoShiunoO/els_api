package com.feec.search.api.tables

import slick.lifted.Tag
import slick.driver.MySQLDriver.api._

/**
 * SynonymT
 */
class SynonymT(tag: Tag) extends Table[(String, Int, String)](tag, "synonym") {
  def primarykey = column[String]("primarykey", O.SqlType("VARCHAR(100)"))
  def channel = column[Int]("channel")
  def synonym = column[String]("synonym", O.SqlType("VARCHAR(100)"))


  // Every table needs a * projection with the same type as the table's type parameter
  def * = (primarykey, channel, synonym)
}
