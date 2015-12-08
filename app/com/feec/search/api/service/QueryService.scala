package com.feec.search.api.service

/**
 * PresearchService
 */
object QueryService {
  def mainQuery(primary: String, alternatives: Seq[String]) = {
    val numAlt = alternatives.length
    val matchAlt = alternatives.map(w => matchBlock(w, 0.5 / numAlt)).mkString(",")

    s"""{
           |  "bool": {
           |            "should": [
           |            ${matchBlock(primary, 1)},
           |            $matchAlt
           |            ]
           |        }
           |}
     """.stripMargin
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