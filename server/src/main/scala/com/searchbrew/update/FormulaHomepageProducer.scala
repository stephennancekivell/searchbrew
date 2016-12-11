package com.searchbrew.update

import java.io.File

import com.searchbrew.Formula
import scala.io.Source

object FormulaHomepageProducer extends GitRepoSupport {

  def doit(): Seq[Formula] = {
    gitUpdate
    parse
  }

  val repoName = "homebrew-core"
  val repoUrl = "https://github.com/Homebrew/homebrew-core.git"

  def parse: Seq[Formula] = {
    val files = new File(repoDir, "homebrew-core/Formula").listFiles()
    println(s"$repoName found ${files.length} files")

    files.map(fileToFormula)
  }

  private def fileToFormula(file: File) = {
    val source = Source.fromFile(file)("UTF8")
    val lines = source.getLines().toList
    val name = file.getName.replaceFirst(".rb$", "")

    val homepage = lines.find(line => line.trim.startsWith("homepage")).map(line => {
      line.replaceFirst("homepage", "").replaceAll("'", "").replaceAll("\"", "").trim
    })

    val desc = lines.find(line => line.trim.startsWith("desc")).map(line => {
      line.replaceFirst("desc", "").replaceAll("''", "").replaceAll("\"", "").trim
    })

    source.close()

    Formula(name, homepage = homepage, description = desc)
  }
}
