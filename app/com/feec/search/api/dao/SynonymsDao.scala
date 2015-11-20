package com.feec.search.api.dao

import java.sql.Connection

import com.feec.search.api.common.enum.SynonymsType
import com.feec.search.api.models.{SynonymsMain, SynonymsRelated}


object SynonymsDao {
  val synonymsSql = "select * from synonyms_main m where m.status = 0 and exists (select * from  synonyms_related r where m.id = r.main_id and r.type = 'SYN' and r.status = 0 and r.related_terms = ? )"
  val relatedSql = "select * from synonyms_related o where o.type = 'REL' and o.status = 0 and o.main_id in (select main_id from  synonyms_related where related_terms =  ?)"


  def synonyms(word: String, conn: Connection) = {
    Query.storeToSingle(() => conn) { conn =>
      val ps = conn.prepareStatement(synonymsSql)
      ps.setString(1, word)
      ps
    } {
      rs =>
        SynonymsMain(rs.getInt("id"), rs.getString("original_terms"), rs.getInt("status"))

    }(false)

  }

  def synonymsRelated(word: String, conn: Connection) = {
    Query.storeToList(() => conn) { conn =>
      val ps = conn.prepareStatement(relatedSql)
      ps.setString(1, word)
      ps
    } {
      rs =>
        SynonymsRelated(rs.getInt("main_id"), rs.getString("related_terms"), SynonymsType.valueOf(rs.getString("type")), rs.getInt("status"))

    }(false)

  }

}
