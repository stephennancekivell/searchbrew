

object ObjectInJs {
  val foo = "hello js stephen"
}

import scala.scalajs.js
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExport


object ScalaJSExample extends js.JSApp {
  def main(): Unit = {
    //TodoReact.go
    //searchbrew.react.Main.go
    searchbrew.angular.Main.main()
  }
}


@JSExport
object Service {
  val re = """.+\@.+\..+""".r

  @JSExport
  def isValid(email: String): Boolean = {
    re.findFirstIn(email).isDefined
  }

}