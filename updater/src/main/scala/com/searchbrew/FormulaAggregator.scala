package com.searchbrew

import scala.concurrent.duration._

import akka.actor._
import akka.contrib.pattern._
import play.Logger
import collection.mutable.ArrayBuffer
import akka.contrib.pattern.Aggregator
import com.searchbrew.FormulaList

case object TimedOut

class FormulaAggregator extends Actor with Aggregator  {
  import context.dispatcher

  val maxBatchSize = 1000

  val values = ArrayBuffer.empty[Formula]

  def esActor = context.actorSelection("../elasticSearchActor")

  var cancel: Option[Cancellable] = None

  Logger.info("FormulaAggregator created")

  def timeout {
    def make {
      cancel = Some(context.system.scheduler.scheduleOnce(5.seconds, self, TimedOut))
    }
    cancel match {
      case None => make
      case Some(cancellable) => {
        cancellable.cancel()
        make
      }
    }
  }

  val handle = expect {
    case f: Formula ⇒
      timeout
      values += f
      if (values.size >= maxBatchSize) processList
    case TimedOut ⇒ {
      Logger.info("Formula Aggregator TimeOut")
      processList
    }
  }

  def processList {
    Logger.info("FormulaAggregator processList")
    val list = values.toList
    values.clear
    if (list.nonEmpty) esActor ! FormulaList(list)
  }
}
