name := """searchbrew"""

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "org.apache.lucene" % "lucene-core" % "4.10.4",
  "org.apache.lucene" % "lucene-queryparser" % "4.10.4",
  "org.apache.lucene" % "lucene-analyzers-common" % "4.10.4",
  "de.heikoseeberger" %% "akka-http-circe" % "1.26.0",
  "org.specs2" %% "specs2" % "2.5" % Test
)

val circeVersion = "0.11.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

javacOptions ++= Seq("-source", "11", "-target", "11")

enablePlugins(DockerPlugin)

enablePlugins(JavaServerAppPackaging)

dockerBaseImage := "openjdk:11-jre-slim"
dockerExposedPorts += 9000
javaOptions in Universal ++= Seq("-J-Xmx256m", "-J-Xms32m")

scalacOptions in Test ++= Seq("-Yrangepos")
