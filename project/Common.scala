package ml.combust.pachyderm

import sbt._
import sbt.Keys._

object Common {
  lazy val defaultSettings: Seq[Def.Setting[_]] = buildSettings

  lazy val buildSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.11.8",
    crossScalaVersions := Seq("2.11.8"),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
  )
}