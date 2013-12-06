package com.searchbrew

import play.api.mvc._
import play.api.libs.concurrent.Akka
import akka.actor.{Props}
import scala.concurrent.duration._
import play.api.Play.current
import play.api.libs.EventSource
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.api.libs.ws.WS

import play.Logger

import play.api.libs.json._

object Application extends Controller {

  val searchActor = Akka.system.actorOf(Props[MainSearchActor])

  def index = Action {
    Ok(com.searchbrew.view.html.index())
  }

  def search = Action.async { request =>
    val size = request.getQueryString("size").getOrElse("100").toInt
    val q = request.getQueryString("q").getOrElse("")

    val queryObj = q match {
      case "" => Json.obj()
      /*
     case q => Json.obj(
       "query" -> Json.obj(
         "fuzzy_like_this" -> Json.obj(
           "like_text" -> q,
           "min_similarity" -> 2
         )
       )
     )
     */
      /*
        match on title boosts title field.
        Needed or fcgiwrap is comes before nginx, having same number off 'nginx' occurences

        fuzzy on all fields to get a range.

        match all docs to boost exact match.


        nginx is a good test case.
       */
      case q => Json.obj(
        "query" -> Json.obj(
          "bool" -> Json.obj(
            "should" -> Json.arr(
              Json.obj(
                "query_string" -> Json.obj(
                  "query" -> JsString(q+"~")
                )
              ),
              Json.obj(
                "query_string" -> Json.obj(
                  "query" -> q
                )
              ),
              Json.obj(
                "match" -> Json.obj(
                  "title" -> q
                )
              )
            )
          )
        )
      )
    }

    val fq = queryObj ++ Json.obj(
      "size" -> size,
      "fields" -> Json.arr("filename", "title", "homepage", "description")
    )

    Logger.info("query "+fq)

    WS.url("http://localhost:9200/formula/formula/_search").post(fq).map {
      response =>
        Ok(response.json.as[JsObject] ++ Json.obj("query" -> JsString(q)))
    }
  }
}