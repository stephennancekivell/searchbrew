package searchbrew.angular

import com.greencatsoft.angularjs.{inject, Angular, injectable, Controller}
import com.greencatsoft.angularjs.core.{HttpService, Scope}
import org.scalajs.dom.ext.Ajax
import searchbrewshared.{FormulaPickle, Formula}
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import js.JSConverters._
import scala.scalajs.js.annotation.JSExportAll
import scala.util.Success

trait SearchbrewScope extends Scope {
  var searchResults: js.Array[js.Dictionary[String]] = js.native
  var searchQuery: String = js.native
  var search: js.Function = js.native
}

object SearchCtrl extends Controller {

  override val name = "SearchCtrl"

  override type ScopeType = SearchbrewScope

  @inject
  var http: HttpService = _

  override def initialize(scope: ScopeType): Unit = {
    scope.searchQuery  = ""
    scope.search = search

    val queryWatcher = (query: Any) => {
      query match {
        case s: String if !s.isEmpty => search()
        case _ => ()
      }
    }

    //scope.$watch("searchQuery", queryWatcher, false)
  }

  def formulaToDict(formula: Formula): js.Dictionary[String] = {
    js.Dictionary(
      "title" -> formula.title,
      "homepage" -> formula.homepage.getOrElse(""),
      "description" -> formula.description.getOrElse("")
    )
  }

  val search = () => {
    Ajax.get("/search.pickle?q="+scope.searchQuery).map { resp =>
      val searchResult = FormulaPickle.r(resp.responseText)

      if (searchResult.query.contains(scope.searchQuery)) {
        scope.searchResults = searchResult.data.map(formulaToDict).toJSArray
        scope.$apply()
      }
    }
  }
}

object Main {
  def main(): Unit ={
    val module = Angular.module("searchbrew")

    module.controller(SearchCtrl)
  }
}