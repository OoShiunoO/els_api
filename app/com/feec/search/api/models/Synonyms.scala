package com.feec.search.api.models

import com.feec.search.api.common.enum.SynonymsType.SynonymsType

case class SynonymsMain(id: Int, originalTerms: String, status: Int)

case class SynonymsRelated(mainId: Int, relatedTerm: String, communication: SynonymsType, status: Int)
