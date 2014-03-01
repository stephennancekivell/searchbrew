package com.searchbrew

import akka.actor.{ActorRef, Actor}
import play.api.libs.ws.WS
import play.api.libs.json.{JsString, JsArray, JsValue, Json}
import java.util.{Date, UUID}
import scala.concurrent.ExecutionContext
import play.Logger

class ElasticsearchActor extends Actor {
  import ExecutionContext.Implicits.global

  def receive = {
    //case fp: FormulaHomepage => indexPage(fp.title, fp.homepageText)
    case descriptions: FormulaDescriptions => indexDescs(descriptions)
    case fl: FormulaList => index(fl)
  }

  val baseUrl = "http://localhost:9200"

  def indexPage(title: String, homepageText: String) {
    Logger.info("indexing homepage "+title)
    val text = Json.toJson(homepageText)
    WS.url(s"$baseUrl/formula/formula/$title/_update").post(
      Json.obj(
        "doc" -> Json.obj(
          "homepageText" -> text
        ),
        "doc_as_upsert" -> true
      )).map {
      response =>
        Logger.info(s"index homepage ${response.status} ${response.body}")
    }
  }

  def index(fl: FormulaList){
    val js = fl.list.map { f =>
      s"""{"update":{"_index":"formula", "_type":"formula", "_id": "${f.title}"}}"
          {"doc":{"homepage": ${JsString(f.homepage)}, "title": ${JsString(f.title)} }, "doc_as_upsert": true}
      """
    }.mkString("")

    bulkIndex(js, "FormulaList")
  }

  def indexDescs(descriptions: FormulaDescriptions) {
    val js = descriptions.list.map { fd =>
      s"""{"update":{"_index":"formula", "_type":"formula", "_id": "${fd.title}"}}"
          {"doc":{"description": ${JsString(fd.description)}, "title": ${JsString(fd.title)}}, "doc_as_upsert": true}
      """
    }.mkString("")

    bulkIndex(js, "FormulaDescriptions")
  }

  def bulkIndex(data: String, action: String) {
    Logger.info(s"index $action")
    Logger.debug(data.take(200))

    WS.url(s"$baseUrl/_bulk").post(data).map {
      response =>
        response.status match {
          case status if status > 300 => Logger.error(s"index $action status: ${response.status}\nresponse.body: ${response.body}\nrequest.body: ${data}}")
          case _ => Logger.info(s"index $action ${response.status} ${response.body.take(50)}}")
        }
    }
  }
}
