package com.searchbrew.update

import java.io.File
import com.searchbrew.Formula
import play.api.{Logger, Play}
import play.api.Play.current
import scala.sys.process.Process
import scala.io.Source


object FormulaDescriptionProducer extends GitRepoSupport {

  def doit(): Seq[Formula] = {
    gitUpdate
    getDescs
  }

  val repoUrl = "https://github.com/telemachus/homebrew-desc.git"
  val repoName = "homebrew-desc"

  def getDescs = {
    val sc = Source.fromFile(new File(repoDir, "homebrew-desc/cmd/brew-desc.rb"), "UTF-8")
    val descs = sc.getLines().
      toList.
      filter(_.contains("=>")).
      map(_.replaceAll(""""""", "")).
      map(_.split("=>")).
      map { arr =>
        arr match {
          case Array(a,b) => Formula(a.trim, description = Some(b.trim.replaceAll(",", "")))
        }
      }
    sc.close()

    Logger.info(s"getDescs  ${descs.length}")
    descs
  }
}