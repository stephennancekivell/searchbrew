package searchbrew.react

import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react._

import org.scalajs.dom.document
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

import searchbrewshared.FormulaPickle

import org.scalajs.dom.ext.{AjaxException, Ajax}


// https://github.com/lihaoyi/scala-js-fiddle/blob/master/client/src/main/scala/fiddle/Client.scala

object Main {
  
  val SearchResultList = ReactComponentB[List[String]]("TodoList")
    .render(props => {
      def createItem(itemText: String) = <.li(itemText)
      <.ul(props map createItem)
    })
    .build

  case class State(items: List[String], query: String)

  class Backend($: BackendScope[Unit, State]) {
    def onChange(e: ReactEventI) =
      $.modState(_.copy(query = e.target.value))
    def handleSubmit(e: ReactEventI) = {
      e.preventDefault()
      $.modState(s => State(s.items :+ s.query, ""))
    }

    def setItems(e: ReactEventI) = {
      e.preventDefault()

      val query = $.state.query

      Ajax.get("/searchPickle?q="+query).map { resp =>
        val li = FormulaPickle.r(resp.responseText).data
        $.modState(s => s.copy(items = li.toList))
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
      <.form(^.onSubmit ==> B.setItems,
        <.input(^.onChange ==> B.onChange, ^.value := S.query),
        <.button("Add #", S.items.length + 1)
      ),
      SearchResultList(S.items)
    )
    ).buildU

  def go(): Unit = {
    React.render(TodoApp(), document.body)
  }
}