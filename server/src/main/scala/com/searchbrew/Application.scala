package com.searchbrew

import akka.actor.{Actor, Props}

import com.searchbrew.update._

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._

import scala.io.Source

import scala.concurrent.duration._

import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.CirceSupport.circeToEntityMarshaller

object Application extends scala.App {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def indexBoth {
    val findHomes = timing("findhome") { FormulaHomepageProducer.doit() }
    val bigString = findHomes.map(f => Seq(f.title, f.description, f.homepage).mkString(",")).mkString("|")
    println("big string "+bigString.length)
    timing("index home") { Index.insert(findHomes) }
  }

  def timing[A](msg: String)(fn: => A): A = {
    val start = System.currentTimeMillis()
    val re = fn
    println(msg+s" took ${System.currentTimeMillis() - start}ms")
    re
  }

  val tickActor = system.actorOf(Props(new Actor {
    def receive = {
      case _ => indexBoth
    }
  }))

  system.scheduler.schedule(
    5 seconds,
    1 hours,
    tickActor,
    "tick"
  )

  def readFile(p: String): Option[String] = {
    val path = p.startsWith("/") match {
      case true => "/static" + p
      case false => "/static/" + p
    }
    println("reading "+path)
    val stream = Option(getClass.getResourceAsStream(path))
    stream.map(Source.fromInputStream(_).getLines().mkString("\n"))
  }

  private def getExtensions(fileName: String) : String = {
    val index = fileName.lastIndexOf('.')
    if(index != 0) {
      fileName.drop(index+1)
    } else
      ""
  }

  val route = {
    path("search") {
      get {
        parameters("q".? ) { q =>
          val found = Index.query(q)
          complete(SearchResponse(q.getOrElse(""), found))
        }
      }
    } ~
     get {
       entity(as[HttpRequest]) { requestData =>
         complete {
           val fullPath = requestData.uri.path.toString match {
             case "/" | "" => "index.html"
             case _ => requestData.uri.path.toString
           }

           val maybeContent = readFile(fullPath)

           maybeContent match {
             case None =>
               HttpResponse(NotFound)
             case Some(content) =>
               val ext = getExtensions(fullPath)
               val maybeContentType = MediaTypes.forExtensionOption(ext)
                 .flatMap {
                   case t: MediaType.WithFixedCharset => Some(ContentType(t))
                   case o: MediaType.WithOpenCharset => Some(ContentType(o, HttpCharsets.`UTF-8`))
                   case other =>
                     println("unknown content type for "+other.getClass.getName)
                     None
                 }

               val entity = maybeContentType.map(HttpEntity(_, content)).getOrElse(HttpEntity(content))
               HttpResponse(OK, entity = entity)
           }
         }
       }
     }
  }

  val logger = Logging(system, getClass)

  val bindingFuture = Http().bindAndHandle(route, AppConfig.http.interface, AppConfig.http.port)

  bindingFuture.onComplete { _ =>
    println("listening on "+AppConfig.http.port)
  }

  sys.addShutdownHook(system.terminate())
}

case class SearchResponse(query: String, data: Seq[Formula])

object AppConfig {
  object http {
    val interface = "0.0.0.0"
    val port = 8080
  }
}