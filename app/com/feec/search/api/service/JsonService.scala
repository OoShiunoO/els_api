package com.feec.search.api.service

import com.feec.search.api.common.enum.Platform.Platform
import com.feec.search.api.common.enum._
import com.feec.search.api.common.utils.TransformUtils
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object JsonService {
  val picDomain = "https://img.gohappy.com.tw/images/product"
  val emptySearchResponse = Json.obj().transform(
    (__ \ 'data).json.put(Json.obj("total" -> JsNumber(0), "hits" -> Json.arr(), "aggregations" -> Json.arr()))).get

  def addHeader(jsonObj: JsObject, code: ReturnCode.Code, took: Long) = {
    Json.obj("statuc_code" -> code.statusCode, "status_mesasge" -> code.message, "took" -> took) ++ jsonObj
  }


  def jsonFormat(jsonObj: JsObject, platform: Platform) = platform match {
    case Platform.Web => jsonObj
    case Platform.App =>
      val trans = (__ \ 'data).json.pickBranch(
        (__ \ 'total).json.pickBranch and
          (__ \ 'products).json.copyFrom((__ \ 'hits).json.pick(of[JsArray].map { case JsArray(arr) => JsArray(arr.map {
            obj =>
              val mainCategory = obj.transform((__ \ 'main_category_id).json.pick).get.as[Int]
              val allCategoryPathId = obj.transform((__ \ 'all_category_path_id).json.pick).get.as[Seq[String]]
              val tmp = allCategoryPathId.filter {
                _.contains(mainCategory.toString)
              }.head
              val sid = TransformUtils.parseInt(tmp.substring(1, tmp.indexOf(":"))).getOrElse(-1)

              val pictureUrl = picDomain + obj.transform((__ \ 'picture).json.pick(of[JsArray].map { case JsArray(arr) => JsArray(arr.filter {
                _.transform((__ \ 'type_id).json.pick).get.as[Int] == 3
              }.map(_.transform((__ \ 'picture_url).json.pick).get))
              })).get.as[Seq[String]].head
              println(pictureUrl)

              val memberPrice = obj.transform((__ \ 'member_price).json.pick).get
              val onsalePrice = obj.transform((__ \ 'onsale_price).json.pick).get

              val correctPrice = onsalePrice match {
                case JsNull => println("nullable")
                  memberPrice
                case _ => onsalePrice
              }

              println(s"$onsalePrice ,$memberPrice,  $correctPrice")


              obj.transform(
                (__ \ 'sid).json.put(JsNumber(sid)) and
                  (__ \ 'cid).json.copyFrom((__ \ 'main_category_id).json.pick) and
                  (__ \ 'pid).json.copyFrom((__ \ 'product_id).json.pick) and
                  (__ \ 'spid).json.copyFrom((__ \ 'shelf_id).json.pick) and
                  (__ \ 'name).json.copyFrom((__ \ 'product_name).json.pick) and
                  (__ \ 'badge).json.copyFrom((__ \ 'name_label).json.pick) and
                  (__ \ 'img).json.put(JsString(pictureUrl)) and
                  (__ \ 'price).json.put(correctPrice.as[JsNumber]) and
                  (__ \ 'type).json.copyFrom((__ \ 'product_type).json.pick) and
                  (__ \ 'restricted).json.copyFrom((__ \ 'restricted).json.pick) and
                  (__ \ 'soldout).json.copyFrom((__ \ 'sold_out).json.pick)
                  reduce

              ).get
          })
          })) and
          (__ \ 'filter).json.copyFrom((__ \ 'aggregations).json.pick(of[JsArray].map {
            case JsArray(arr) => JsArray(arr.filter {
              _.transform((__ \ 'category_path_name).json.pick).get.as[String].split("　>　").length == 1
            }.map {
              _.transform((__ \ 'id).json.copyFrom((__ \ 'category_path_id).json.pick)
                and (__ \ 'name).json.copyFrom((__ \ 'category_path_name).json.pick)
                and (__ \ 'count).json.copyFrom((__ \ 'doc_count).json.pick) reduce).get
            })
          })
          )
          reduce

      )

      val result = jsonObj.transform(trans)

      result.get
  }

}

