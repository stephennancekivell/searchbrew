
package com.searchbrew.update

import java.io.File
import scala.sys.process.Process
import play.{Logger, Play}

trait GitRepoSupport {

  def repoUrl: String

  def repoName: String

  val repoDir = new File(Play.application.path, "git.repo")
  repoDir.mkdir()

  def gitUpdate {
    val output = if (!new File(repoDir, repoName).exists()){
      Logger.info(s"cloning repo $repoName")
      Process(Seq("git", "clone", repoUrl), repoDir).!!
    } else {
      Logger.info("repo already exists, not cloning")
      Process(Seq("git", "pull"), new File(repoDir, repoName)).!!
    }

    Logger.info(s"git $repoName $output")
  }
}
