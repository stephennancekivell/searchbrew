name := """searchbrew"""

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.apache.lucene" % "lucene-core" % "4.9.1",
  "org.apache.lucene" % "lucene-queryparser" % "4.9.1",
  "org.apache.lucene" % "lucene-analyzers-common" % "4.9.1",
  specs2 % Test
)

unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala"
unmanagedSourceDirectories in Test    += baseDirectory.value / "src" / "test" / "scala"

enablePlugins(PlayScala)

enablePlugins(SbtNativePackager)

enablePlugins(JavaServerAppPackaging)

enablePlugins(DockerPlugin)