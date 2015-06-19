package com.searchbrew

import akka.actor.{Actor, Props}
import play.api._
import play.api.libs.json.Json
import play.api.mvc._

import com.searchbrew.update._
import play.libs.Akka

import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {


  implicit val fdWrites = Json.writes[Formula]
  implicit val searchResultWrites = Json.writes[SearchResult]

  def search(q: Option[String]) = Action {
    Ok(
      Json.toJson(
      SearchResult(
        query = q,
        data = Index.query(q))
      )
    )
  }

  def searchPickle(q: Option[String]) = Action {
    val data = Index.query(q).map { fd =>
      searchbrewshared.Formula(fd.title, fd.homepage, fd.description)
    }

    val rendered =
      searchbrewshared.FormulaPickle.w(
          searchbrewshared.SearchResult(
          query = q,
          data = data)
        )

    Ok(rendered)
  }

  def indexBoth {
    Index.insertHomepages(FormulaHomepageProducer.doit())
    Index.insertDescriptions(FormulaDescriptionProducer.doit())
  }

  val tickActor = Akka.system.actorOf(Props(new Actor {
    def receive = {
      case _ => indexBoth
    }
  }))

  Akka.system.scheduler.schedule(
    5 seconds,
    1 hours,
    tickActor,
    "tick"
  )


  def index2 = Action {
    Ok(views.html.index2())
  }
}