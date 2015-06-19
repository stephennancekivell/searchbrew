package searchbrew.react

import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react._

import org.scalajs.dom.document
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

import searchbrewshared.{FormulaPickle, SearchResult, Formula}

import org.scalajs.dom.ext.{AjaxException, Ajax}

// https://github.com/lihaoyi/scala-js-fiddle/blob/master/client/src/main/scala/fiddle/Client.scala

object Main {
  
  val SearchResultList = ReactComponentB[Seq[Formula]]("TodoList")
    .render(props => {
      def createItem(fd: Formula) =
        <.tr(
          <.td(
            <.a(
              ^.href :=fd.homepage,
              fd.title
              )
          ),
          <.td(
            <.p(fd.description)
            )
          )

      <.table(
        <.tbody(
          (props map createItem)
        )
      )
    })
    .build

  case class State(items: Seq[Formula], query: String)

  class Backend($: BackendScope[Unit, State]) {
    def onChange(e: ReactEventI) =
      $.modState(_.copy(query = e.target.value))
    
    def search(e: ReactEventI) = {
      e.preventDefault()

      val query = $.state.query

      Ajax.get("/search.pickle?q="+query).map { resp =>
        val searchResult = FormulaPickle.r(resp.responseText).data
        $.modState(s => s.copy(items = searchResult))
      }

      $.modState(_.copy(query = $.state.query))
    }
  }

  val TodoApp = ReactComponentB[Unit]("TodoApp")
    .initialState(State(Nil, ""))
    .backend(new Backend(_))
    .render((_,S,B) =>
    <.div(
      <.h1("searchbrew"),
      <.form(^.onSubmit ==> B.search,
        <.input(^.onChange ==> B.onChange, ^.value := S.query)
      ),
      SearchResultList(S.items)
    )
    ).buildU

  def go(): Unit = {
    React.render(TodoApp(), document.body)
  }
}