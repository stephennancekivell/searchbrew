name := """searchbrew"""

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "org.apache.lucene" % "lucene-core" % "4.9.1",
  "org.apache.lucene" % "lucene-queryparser" % "4.9.1",
  "org.apache.lucene" % "lucene-analyzers-common" % "4.9.1",
  "de.heikoseeberger" %% "akka-http-circe" % "1.25.2",
  "org.specs2" %% "specs2" % "2.4.17" % Test
)

val circeVersion = "0.11.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

enablePlugins(DockerPlugin)

enablePlugins(JavaServerAppPackaging)

scalacOptions in Test ++= Seq("-Yrangepos")

dockerExposedPorts += 9000