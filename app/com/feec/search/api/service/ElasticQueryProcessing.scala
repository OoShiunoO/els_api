package com.feec.search.api.service

import com.feec.search.api.tables.SynonymT
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * QueryProcessing
 */
object ElasticQueryProcessing {
  def mainQuery(primary: String, dbConfig: DatabaseConfig[JdbcProfile]): Future[String] = {
    /** Get synonym from database */
    val synonym = TableQuery[SynonymT]

    val tokens = primary.split("　").toList
    val tails = if (tokens.length == 1) ""
                else "　" + tokens.tails.mkString("　")

    def query = synonym.filter(x =>
      x.primarykey === tokens.head).map(w => (w.channel, w.synonym)).result
    val response = dbConfig.db.run(query)

    response.map{ s =>
      val numAlt = s.length
      val channel = if (numAlt == 0)  0 else s.head._1

      if (numAlt != 0 && channel == 2) {
        val matchAlt1 = s.map(w => matchBlock(w._2 + tails, 0.8)).mkString(",")

        s"""{
            |  "bool": {
            |            "should": [
            |            ${matchBlock(primary, 1)},
            |            $matchAlt1
            |            ]
            |        }
            |}
     """.stripMargin
      } else if (numAlt != 0 && channel == 1) {
        val matchAlt2 = s.map(w => matchBlock(w._2 + tails, 1)).mkString(",")

        s"""{
           |  "bool": {
           |            "should": [
           |            $matchAlt2
            |            ]
            |        }
            |}
     """.stripMargin
      } else {
        s"""{
            |  "bool": {
            |            "should": [
            |            ${matchBlock(primary, 1)}
            |            ]
            |        }
            |}
     """.stripMargin
      }
    }


  }

  def matchBlock(key: String, scale: Double) = {
    val boost1 = 1.0 * scale
    val boost2 = 2.3333 * scale
    val boost3 = 2.0 * scale
    val boost4 = 2.0 * scale
    val boost5 = 1.667 * scale
    val boost6 = 1.667 * scale
    s"""
           |                { "match": { "all_category_path_name":
           |                    {"query":"$key",
           |                    "operator" : "or",
           |                      "boost" : $boost1
           |                    }
           |                  }
           |                },
           |                { "match": { "product_name":
           |                    {"query":"$key",
           |                    "operator" : "or",
           |                      "boost" : $boost1
           |                    }
           |                  }
           |                },
           |                { "match": { "desc_brief":
           |                    {"query":"$key",
           |                    "operator" : "or",
           |                      "boost" : $boost1
           |                    }
           |                  }
           |                },
           |                { "match": { "author":
           |                    {"query":"$key",
           |                    "operator" : "or",
           |                      "boost" : $boost1
           |                    }
           |                  }
           |                },
           |                { "match": { "publisher":
           |                    {"query":"$key",
           |                    "operator" : "or",
           |                      "boost" : $boost1
           |                    }
           |                  }
           |                },
           |                { "match": { "isbn":
           |                    {"query":"$key",
           |                    "operator" : "or",
           |                      "boost" : $boost1
           |                    }
           |                  }
           |                }
     """.stripMargin
  }

}