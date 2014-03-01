package com.searchbrew.update

import java.io.File
import akka.actor.{Props, Actor}
import play.api.{Logger, Play}
import play.api.Play.current
import scala.sys.process.Process
import scala.io.Source
import com.searchbrew.update._
import com.searchbrew._

class FormulaDescriptionActor extends Actor with GitRepoSupport {

  def esActor = context.actorSelection("../elasticSearchActor")

  def receive = {
    case Tick => {
      gitUpdate
      val decs = getDescs
      esActor ! decs

      this.sender ! "done FormulaDescription"
    }
  }

  val repoUrl = "https://github.com/telemachus/homebrew-desc.git"
  val repoName = "homebrew-desc"

  def getDescs = {
    val file = new File(repoDir, "homebrew-desc/brew-desc.rb")
    val sc = Source.fromFile(new File(repoDir, "homebrew-desc/brew-desc.rb"), "UTF-8")
    val descs = sc.getLines().toList.filter(_.contains("=>")).map(_.replaceAll(""""""", "")).map(_.split("=>")).map{ arr =>
      arr match {
        case Array(a,b) => FormulaDescription(a.trim,b.trim.replaceAll(",", ""))
      }
    }
    sc.close()

    Logger.info(s"getDescs ${descs.take(5)}")
    FormulaDescriptions(descs)
  }
}