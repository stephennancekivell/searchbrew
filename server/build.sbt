import sbt.Project.projectToRef

lazy val scalaV = "2.11.8"

name := """searchbrew"""

lazy val server = (project in file("jvm")).settings(
  scalaVersion := scalaV,
  name := "searchbrew",
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    "org.apache.lucene" % "lucene-core" % "4.9.1",
    "org.apache.lucene" % "lucene-queryparser" % "4.9.1",
    "org.apache.lucene" % "lucene-analyzers-common" % "4.9.1",
    specs2 % Test
  ),
  unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala",
  unmanagedSourceDirectories in Test    += baseDirectory.value / "src" / "test" / "scala"
).enablePlugins(PlayScala)

onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value

enablePlugins(SbtNativePackager)

enablePlugins(JavaServerAppPackaging)

enablePlugins(DockerPlugin)