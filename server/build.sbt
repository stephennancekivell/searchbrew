name := """searchbrew"""

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % "10.0.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.0",
  "org.apache.lucene" % "lucene-core" % "4.9.1",
  "org.apache.lucene" % "lucene-queryparser" % "4.9.1",
  "org.apache.lucene" % "lucene-analyzers-common" % "4.9.1",
  "org.specs2" %% "specs2" % "2.4.17" % Test
)

val circeVersion = "0.6.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

enablePlugins(DockerPlugin)

enablePlugins(JavaServerAppPackaging)

scalacOptions in Test ++= Seq("-Yrangepos")
