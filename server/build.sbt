import sbt.Project.projectToRef

lazy val clients = Seq(client)
lazy val scalaV = "2.11.6"

name := """searchbrew"""

lazy val server = (project in file("jvm")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  name := "searchbrew",
  //resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    "com.vmunier" %% "play-scalajs-scripts" % "0.2.2",
    "org.apache.lucene" % "lucene-core" % "4.9.1",
    "org.apache.lucene" % "lucene-queryparser" % "4.9.1",
    "org.apache.lucene" % "lucene-analyzers-common" % "4.9.1",
    "org.webjars" % "react" % "0.12.2",
    "com.lihaoyi" %% "upickle" % "0.2.8"
  ),
  unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala",
  unmanagedSourceDirectories in Test    += baseDirectory.value / "src" / "test" / "scala"
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val client = (project in file("js")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  sourceMapsDirectories += sharedJs.base / "..",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    "com.github.japgolly.scalajs-react" %%% "core" % "0.9.0",
    "com.github.japgolly.scalajs-react" %%% "test" % "0.9.0" % "test",
    "com.lihaoyi" %%% "upickle" % "0.2.8",
    "com.greencatsoft" %%% "scalajs-angular" % "0.4"
  ),
  jsDependencies +=
    "org.webjars" % "react" % "0.12.2" / "react-with-addons.js" commonJSName "React",
  requiresDOM := true,
  scalaJSStage in Test := FastOptStage,
  unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala"
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalaVersion := scalaV,
    unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.2.8"
    )
  ).
  jsConfigure(_ enablePlugins ScalaJSPlay).
  jsSettings(sourceMapsBase := baseDirectory.value / "..")

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
