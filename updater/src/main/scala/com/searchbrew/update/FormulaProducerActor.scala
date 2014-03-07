package com.searchbrew.update


import akka.actor.{Props, Actor}

import java.net.URL
import play.api.libs.json._

import java.io.File
import com.searchbrew._
import com.searchbrew.Tick

import play.api._

import play.api.Play.current
import scala.sys.process._
import scala.io.Source
import play.api.libs.ws.WS
import scala.concurrent.ExecutionContext
import play.libs.Akka

class FormulaProducerActor extends Actor with GitRepoSupport {

  def fAggregator = context.actorSelection("../formulaAggregator")

  def receive =  {
    case Tick => {
      Logger.info("FormulaProducerActor TickOnce")
      gitUpdate
      queueFiles
    }
    case f: File => fAggregator ! fileToFormula(f)
  }

  val repoName = "homebrew"
  val repoUrl = "https://github.com/mxcl/homebrew.git"

  def queueFiles = {
    val files = new File(repoDir, "homebrew/Library/Formula").listFiles()
    Logger.info(s"$repoName found ${files.length} files")
    files.foreach(f =>  context.self ! f)
  }

  def fileToFormula(file: File) = {
    val source = Source.fromFile(file)("UTF8")
    val lines = source.getLines().toList
    val name = file.getName.replaceFirst(".rb$", "")
    val homepage = lines.find(line => line.trim.startsWith("homepage")).map(line => {
      line.replaceFirst("homepage", "").trim.split(" ")(0).replace("'", "")
    })
    source.close()
    Formula(file.getName, name, homepage.get)
  }
}