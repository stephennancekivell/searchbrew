package com.searchbrew

import scala.concurrent.duration._

import akka.actor._
import akka.contrib.pattern._
import play.Logger
import collection.mutable.ArrayBuffer
import akka.contrib.pattern.Aggregator

class FormulaAggregator extends Actor with Aggregator  {
  import context.dispatcher

  case object TimedOut

  val maxBatchSize = 1000

  val values = ArrayBuffer.empty[Formula]

  def esActor = context.actorSelection("../elasticSearchActor")

  context.system.scheduler.scheduleOnce(5.seconds, self, TimedOut)

  Logger.info("FormulaAggregator created")

  val handle = expect {
    case f: Formula ⇒
      values += f
      if (values.size >= maxBatchSize) processList
    case TimedOut ⇒ processList
  }

  def processList {
    Logger.info("processList")
    val list = values.toList
    values.clear
    if (list.nonEmpty) esActor ! FormulaList(list)
  }
}
