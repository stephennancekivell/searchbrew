name := "searchbrew"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.1",
  "org.webjars" % "angularjs" % "1.1.5-1",
  "org.webjars" % "bootstrap" % "2.3.2",
  "com.typesafe.akka" % "akka-contrib_2.10" % "2.2.3"
)     

play.Project.playScalaSettings
