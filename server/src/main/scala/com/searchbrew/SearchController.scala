package com.searchbrew

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

object JsonSearchController extends Controller {
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
}