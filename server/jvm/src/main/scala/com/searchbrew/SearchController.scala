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

object UPickleSearchController extends Controller {

  def search(q: Option[String]) = Action {
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
}
