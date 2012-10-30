import sbt._

object Build extends sbt.Build {
  lazy val distress = Project("distress", file("."))
  lazy val script = Project("distress-app", file("app")) dependsOn(distress)
}
