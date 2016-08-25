package com.searchbrew

import akka.actor.{Actor, Props}
import play.api.mvc._

import com.searchbrew.update._
import play.libs.Akka

import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def angularIndex = Action {
    Ok(views.html.angularIndex())
  }

  def indexBoth {
    Index.insertHomepages(FormulaHomepageProducer.doit())
    Index.insertDescriptions(FormulaDescriptionProducer.doit())
  }

  val tickActor = Akka.system.actorOf(Props(new Actor {
    def receive = {
      case _ => indexBoth
    }
  }))

  Akka.system.scheduler.schedule(
    5 seconds,
    1 hours,
    tickActor,
    "tick"
  )
}