lazy val kanelaKamonExtension = (project in file("."))
  .enablePlugins(JavaAgent)
  .settings(instrumentationSettings)
  .settings(
    moduleName := "kanela-kamon-extension",
    resolvers += Resolver.mavenLocal,
    libraryDependencies ++= Seq(
      "io.kamon" %% "kamon-core"    % "2.0.0-f7bb35e312af1c2bca66270b4a5230d12ce10535",
      "io.kamon" %  "kanela-agent"  % "0.0.17" % "provided",

      scalatest % "test"
    )
  )
  