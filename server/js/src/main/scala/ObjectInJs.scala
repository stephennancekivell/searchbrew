

object ObjectInJs {
  val foo = "hello js stephen"
}

import scala.scalajs.js
import org.scalajs.dom


object ScalaJSExample extends js.JSApp {
  def main(): Unit = {
    searchbrew.react.Todo.go
  }
}
