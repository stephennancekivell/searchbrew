
package com.searchbrew.update

import java.io.File
import scala.sys.process.Process

trait GitRepoSupport {

  def repoUrl: String

  def repoName: String

  val repoDir = new File("git.repo")
  repoDir.mkdir()

  def gitUpdate {
    val output = if (!new File(repoDir, repoName).exists()){
      println(s"cloning repo $repoName")
      Process(Seq("git", "clone", repoUrl), repoDir).!!
    } else {
      println("repo already exists, not cloning")
      Process(Seq("git", "pull"), new File(repoDir, repoName)).!!
    }

    println(s"git $repoName $output")
  }
}
