package com.searchbrew.update


import akka.actor.{Props, Actor}

import java.net.URL
import play.api.libs.json._

import java.io.File
import com.searchbrew._

//import org.apache.commons.io.FileUtils
import play.api._

import play.api.Play.current
import scala.sys.process._
import scala.io.Source
import play.api.libs.ws.WS
import scala.concurrent.ExecutionContext
import play.libs.Akka

class FormulaProducerActor extends Actor with GitRepoSupport {

  def esActor = context.actorSelection("../elasticSearchActor")
  def fAggregator = context.actorSelection("../formulaAggregator")

  def receive =  {
    case Tick => {
      Logger.info("TickOnce")
      gitUpdate
      queueFiles
    }
    case f: File => context.self ! fileToFormula(f)
    case fp: FormulaHomepage => esActor ! fp
    case f: Formula => {
      fAggregator ! f // took 1 minute on searchbrew.com
      //esActor ! f // took 2 minutes
      //context.self ! getFormulaHomepage(f)
    }
  }

  val repoName = "homebrew"
  val repoUrl = "https://github.com/mxcl/homebrew.git"

  def queueFiles = {
    new File(repoDir, "homebrew/Library/Formula").listFiles().foreach(f =>  context.self ! f)
  }

  def fileToFormula(file: File) = {
    val source = Source.fromFile(file)
    val lines = source.getLines().toList
    val name = file.getName.replaceFirst(".rb$", "")
    val homepage = lines.find(line => line.trim.startsWith("homepage")).map(line => {
      line.replaceFirst("homepage", "").trim.split(" ")(0).replace("'", "")
    })
    source.close()
    Formula(file.getName, name, homepage.get)
  }

  //  def getFormulaHomepage(f: Formula){
  //    import ExecutionContext.Implicits.global
  //
  //    Logger.info("getHomepage "+f.title)
  //
  //    WS.url(f.homepage).get().map { response =>
  //      Logger.info("gotHomepage "+f)
  //      context.self ! FormulaHomepage(f.title, Jsoup.parse(response.body).text)
  //    }
  //  }
}