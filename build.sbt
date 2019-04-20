lazy val kamonInstrumentationCommon = (project in file("."))
  .enablePlugins(JavaAgent)
  .settings(instrumentationSettings)
  .settings(
    name := "kamon-instrumentation-common",
    moduleName := name.value,
    resolvers += Resolver.mavenLocal,
    libraryDependencies ++= Seq(
      "io.kamon" %% "kamon-core"    % "2.0.0-M1",
      "io.kamon" %  "kanela-agent"  % "1.0.0-M1" % "provided",

      scalatest % "test"
    )
  )
