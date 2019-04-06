lazy val kanelaKamonExtension = (project in file("."))
  .enablePlugins(JavaAgent)
  .settings(instrumentationSettings)
  .settings(
    moduleName := "kanela-kamon-extension",
    libraryDependencies ++= Seq(
      "io.kamon" %% "kamon-core"    % "1.2.0-b5a79846fc249b16c7bf4fd4219846e266ca1d40",
      "io.kamon" %  "kanela-agent"  % "0.0.16" % "provided",

      scalatest % "test"
    )
  )
