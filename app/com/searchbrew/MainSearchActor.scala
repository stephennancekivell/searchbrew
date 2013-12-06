package com.searchbrew

import akka.actor.{Props, Actor}
import play.api.libs.iteratee.{Concurrent}
import scala.collection.mutable.HashMap
import java.util.UUID
import play.api.libs.json.JsValue

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

import play.Logger
import com.searchbrew.update._

case object Tick

class MainSearchActor extends Actor {
  import ExecutionContext.Implicits.global

  Logger.info("MainSearchActor")

  var channels = new HashMap[UUID, Concurrent.Channel[JsValue]]

  val elasticSearchActor = context.system.actorOf(Props[ElasticsearchActor], "elasticSearchActor")
  val formulaProducerActor = context.system.actorOf(Props[FormulaProducerActor], "formulaProducerActor")
  val formulaDescriptionActor = context.system.actorOf(Props[FormulaDescriptionActor], "formulaDescriptionActor")
  val fAggregator = context.system.actorOf(Props[FormulaAggregator], "formulaAggregator")

  val cancellable = context.system.scheduler.schedule(0 second, 12 hour, self, Tick)

  def receive = {
    case formula: Formula => {
      Logger.info("main got formula" + formula)
      elasticSearchActor ! formula
    }
    case Tick => {
      formulaProducerActor ! Tick
      formulaDescriptionActor ! Tick
    }
  }

  override def postStop() {
    cancellable.cancel
    super.postStop
  }
}
