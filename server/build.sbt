import sbt.Project.projectToRef

lazy val clients = Seq(exampleClient)
lazy val scalaV = "2.11.6"

lazy val exampleServer = (project in file("jvm")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  //resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    "org.apache.lucene" % "lucene-core" % "4.9.1",
    "org.apache.lucene" % "lucene-queryparser" % "4.9.1",
    "org.apache.lucene" % "lucene-analyzers-common" % "4.9.1"
  ),
  unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala",
  unmanagedSourceDirectories in Test    += baseDirectory.value / "src" / "test" / "scala"
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(exampleSharedJvm)

lazy val exampleClient = (project in file("js")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  sourceMapsDirectories += exampleSharedJs.base / "..",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0"
  ),
  unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala"
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(exampleSharedJs)

lazy val exampleShared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalaVersion := scalaV,
    unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala"
  ).
  jsConfigure(_ enablePlugins ScalaJSPlay).
  jsSettings(sourceMapsBase := baseDirectory.value / "..")

lazy val exampleSharedJvm = exampleShared.jvm
lazy val exampleSharedJs = exampleShared.js

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project exampleServer", _: State)) compose (onLoad in Global).value
