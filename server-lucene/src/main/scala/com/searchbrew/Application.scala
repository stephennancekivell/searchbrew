package com.searchbrew

import play.api._
import play.api.libs.json.Json
import play.api.mvc._

import com.searchbrew.update._

object Application extends Controller {

  def index = Action {
    Ok("bla")
  }

  implicit val fdWrites = Json.writes[Formula]

  def search(q: Option[String]) = Action {
    Ok(
      Json.toJson(
        Index.query(q)
      )
    )
  }

  def indexHomepage() = Action {
    Index.insertHomepages(FormulaHomepageProducer.doit())
    Ok
  }

  def indexDescriptions() = Action {
    Index.insertDescriptions(FormulaDescriptionProducer.doit())
    Ok
  }

}