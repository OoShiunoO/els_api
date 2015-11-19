package com.feec.search.api.service

import com.feec.search.api.common.utils.{DBUtils, DCConnectionPool}
import com.feec.search.api.dao.SynonymsDao

import scala.util.control.NonFatal


object SynonymsService {
  def keywordSynonyms(key: String): String = {
    lazy val conn = DCConnectionPool.getConnection()

    try {
      val synonymsWord = SynonymsDao.synonyms(key, conn) match {
        case Some(syn) => syn.originalTerms
        case None => key
      }

      SynonymsDao.synonymsRelated(synonymsWord, conn) match {
        case Some(rel) => rel.map {
          _.relatedTerms
        }.mkString(" ")
        case None => synonymsWord
      }
    }
    catch {
      case NonFatal(e) => e.printStackTrace
        key
    } finally {
      DBUtils.close(conn)
    }
  }


}
