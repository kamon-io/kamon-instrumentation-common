lazy val kamonInstrumentationCommon = (project in file("."))
  .enablePlugins(JavaAgent)
  .settings(instrumentationSettings)
  .settings(
    name := "kamon-instrumentation-common",
    moduleName := name.value,
    crossScalaVersions := Seq("2.11.12", "2.12.8", "2.13.0"),
    resolvers += Resolver.mavenLocal,
    libraryDependencies ++= Seq(
      "io.kamon" %% "kamon-core"    % "2.0.0",
      "io.kamon" %  "kanela-agent"  % "1.0.0" % "provided",

      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "org.slf4j" % "slf4j-nop"     % "1.7.25" % "test",
      "io.kamon" %% "kamon-testkit" % "2.0.0" % "test"
    )
  )
