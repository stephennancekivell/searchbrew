package searchbrew.react

import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react._

import org.scalajs.dom.document

object Main {
  val NoArgs =
    ReactComponentB[String]("No args")
      .render(name => <.div("hello", name))
      .build

  def go(): Unit = {
    React.render(NoArgs("react"), document.body)
  }
}

object Todo {
  val TodoList = ReactComponentB[List[String]]("TodoList")
    .render(props => {
      def createItem(itemText: String) = <.li(itemText)
      <.ul(props map createItem)
    })
    .build

  case class State(items: List[String], text: String)

  class Backend($: BackendScope[Unit, State]) {
    def onChange(e: ReactEventI) =
      $.modState(_.copy(text = e.target.value))
    def handleSubmit(e: ReactEventI) = {
      e.preventDefault()
      $.modState(s => State(s.items :+ s.text, ""))
    }
  }

  val TodoApp = ReactComponentB[Unit]("TodoApp")
    .initialState(State(Nil, ""))
    .backend(new Backend(_))
    .render((_,S,B) =>
    <.div(
      <.h3("TODO"),
      TodoList(S.items),
      <.form(^.onSubmit ==> B.handleSubmit,
        <.input(^.onChange ==> B.onChange, ^.value := S.text),
        <.button("Add #", S.items.length + 1)
      )
    )
    ).buildU

  def go(): Unit = {
    React.render(TodoApp(), document.body)
  }
}