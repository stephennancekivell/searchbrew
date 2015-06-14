name := """searchbrew"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.apache.lucene" % "lucene-core" % "4.9.1",
  "org.apache.lucene" % "lucene-queryparser" % "4.9.1",
  "org.apache.lucene" % "lucene-analyzers-common" % "4.9.1"
)

unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala"

unmanagedSourceDirectories in Test += baseDirectory.value / "src" / "test" / "scala"