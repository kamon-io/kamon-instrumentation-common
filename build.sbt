lazy val kanelaKamonExtension = (project in file("."))
  .enablePlugins(JavaAgent)
  .settings(instrumentationSettings)
  .settings(
    name := "kanela-kamon-extension",
    moduleName := name.value,
    resolvers += Resolver.mavenLocal,
    libraryDependencies ++= Seq(
      "io.kamon" %% "kamon-core"    % "2.0.0-20abd7cdb734fa6a5578a274931ec8d5eea9e06d",
      "io.kamon" %  "kanela-agent"  % "0.0.17" % "provided",

      scalatest % "test"
    )
  )
