package com.searchbrew.update

import java.net.URL

import java.io.File

import com.searchbrew.Formula
import play.api._

import play.api.Play.current
import scala.sys.process._
import scala.io.Source
import scala.concurrent.ExecutionContext

object FormulaHomepageProducer extends GitRepoSupport {

  def doit(): Seq[Formula] = {
    gitUpdate
    parse
  }

  val repoName = "homebrew"
  val repoUrl = "https://github.com/mxcl/homebrew.git"

  def parse: Seq[Formula] = {
    val files = new File(repoDir, "homebrew/Library/Formula").listFiles()
    Logger.info(s"$repoName found ${files.length} files")

    files.map(fileToFormula)
  }

  def fileToFormula(file: File) = {
    val source = Source.fromFile(file)("UTF8")
    val lines = source.getLines().toList
    val name = file.getName.replaceFirst(".rb$", "")
    val homepage = lines.find(line => line.trim.startsWith("homepage")).map(line => {
      line.replaceFirst("homepage", "").trim.split(" ")(0).replace("'", "").replace(""""""", "")
    })
    source.close()

    Formula(name, homepage = homepage)
  }
}
