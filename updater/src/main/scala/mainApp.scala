
import akka.actor.Props
import com.searchbrew._
import java.io.File
import play.core.StaticApplication
import play.libs.Akka

object MainApp extends StaticApplication(new File(".")) {

  def main(args: Array[String]) {
    val searchActor = Akka.system.actorOf(Props[MainSearchActor])
  }
}