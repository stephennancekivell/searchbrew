import sbt._
import Keys._
import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{ Dist, outputDirectory, distJvmOptions, distMainClass, additionalLibs}
 
object SearchbrewUpdaterBuild extends Build {
  val Version      = "2.2.3"
  val ScalaVersion = "2.10.2"

  lazy val kernel = Project(
    id = "searchbrew-updater",
    base = file("."),
    settings = buildSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
      outputDirectory in Dist := file("target/dist"),
      distMainClass in Dist := "MainApp",
      additionalLibs in Dist := Seq(new java.io.File("target/dist/deploy/searchbrew-updater_2.10-1.0-SNAPSHOT.jar"))
    )
  )

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    version      := Version,
    scalaVersion := ScalaVersion
  )
}