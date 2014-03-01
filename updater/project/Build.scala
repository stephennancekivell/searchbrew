import sbt._
import Keys._
import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{ Dist, outputDirectory, distJvmOptions}
 
object SearchbrewUpdaterBuild extends Build {
  val Version      = "2.2.3"
  val ScalaVersion = "2.10.2"

  lazy val kernel = Project(
    id = "searchbrew-updater",
    base = file("."),
    settings = buildSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
      // libraryDependencies ++= Dependencies.helloKernel,
      distJvmOptions in Dist := "-Xms256M -Xmx1024M",
      outputDirectory in Dist := file("target/dist")
    )
  )

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    version      := Version,
    scalaVersion := ScalaVersion
  )
}