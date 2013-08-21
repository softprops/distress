organization := "organization"

name := "distress"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"

libraryDependencies += "org.slf4j" % "slf4j-jdk14" % "1.6.2"

publishTo := Some(Opts.resolver.sonatypeStaging)

licenses <<= version(v =>
      Seq("MIT" ->
          url("https://github.com/softprops/distress/blob/%s/LICENSE" format v)))

homepage :=
  Some(new java.net.URL("https://github.com/softprops/distress/"))

publishArtifact in Test := false

publishMavenStyle := true

pomExtra := (
  <scm>
    <url>git@github.com:softprops/distress.git</url>
    <connection>scm:git:git@github.com:softprops/distress.git</connection>
  </scm>
  <developers>
    <developer>
      <id>softprops</id>
      <name>Doug Tangren</name>
      <url>http://github.com/softprops</url>
    </developer>
  </developers>)
