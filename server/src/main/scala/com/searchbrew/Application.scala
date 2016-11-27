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