name := """server-lucene"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  //anorm,
  //cache,
  ws,
  "org.apache.lucene" % "lucene-core" % "4.9.0",
  "org.apache.lucene" % "lucene-queryparser" % "4.9.0",
  "org.apache.lucene" % "lucene-analyzers-common" % "4.9.0"
)

unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala"