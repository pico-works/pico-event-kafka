import sbt.Keys._
import sbt._

object Build extends sbt.Build {  
  val pico_atomic               = "org.pico"        %%  "pico-atomic"               % "0.2.0-5"
  val pico_disposal             = "org.pico"        %%  "pico-disposal"             % "0.5.0-8"
  val pico_fp                   = "org.pico"        %%  "pico-fp"                   % "0.0.1-3"

  val specs2_core               = "org.specs2"      %%  "specs2-core"               % "3.7.2"

  implicit class ProjectOps(self: Project) {
    def standard(theDescription: String) = {
      self
          .settings(scalacOptions in Test ++= Seq("-Yrangepos"))
          .settings(publishTo := Some("Releases" at "s3://dl.john-ky.io/maven/releases"))
          .settings(description := theDescription)
          .settings(isSnapshot := true)
    }

    def notPublished = self.settings(publish := {}).settings(publishArtifact := false)

    def libs(modules: ModuleID*) = self.settings(libraryDependencies ++= modules)

    def testLibs(modules: ModuleID*) = self.libs(modules.map(_ % "test"): _*)
  }

  lazy val `pico-event-shim-kafka` = Project(id = "pico-event-shim-kafka", base = file("pico-event-shim-kafka"))
      .standard("Tiny publish-subscriber library")
      .libs(pico_atomic, pico_disposal, pico_fp)
      .testLibs(specs2_core)

  lazy val all = Project(id = "pico-event-shim-kafka-project", base = file("."))
      .notPublished
      .aggregate(`pico-event-shim-kafka`)
}
