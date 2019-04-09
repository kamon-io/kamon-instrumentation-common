lazy val kanelaKamonExtension = (project in file("."))
  .enablePlugins(JavaAgent)
  .settings(instrumentationSettings)
  .settings(
    moduleName := "kanela-kamon-extension",
    resolvers += Resolver.mavenLocal,
    libraryDependencies ++= Seq(
      "io.kamon" %% "kamon-core"    % "2.0.0-7dd537de1b5654d5f0d019f076edccb757775f4d",
      "io.kamon" %  "kanela-agent"  % "0.0.17-SNAPSHOT" % "provided",

      scalatest % "test"
    )
  )
  