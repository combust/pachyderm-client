package ml.combust.pachyderm

import com.typesafe.sbt.SbtPgp.autoImportImpl._
import com.typesafe.sbt.pgp.PgpKeys._
import xerial.sbt.Sonatype.autoImport._
import sbt._
import sbt.Keys._

object Common {
  lazy val defaultSettings: Seq[Def.Setting[_]] = buildSettings

  lazy val buildSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.11.8",
    crossScalaVersions := Seq("2.11.8", "2.12.1"),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    organization := "ml.combust.pachyderm"
  )

  lazy val sonatypeSettings: Seq[Def.Setting[_]] = Seq(
    sonatypeProfileName := "ml.combust",
    publishMavenStyle in publishSigned := true,
    publishTo in publishSigned := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    licenses := Seq("Apache 2.0 License" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
    homepage := Some(url("https://github.com/combust/pachyderm-client")),
    scmInfo := Some(ScmInfo(url("https://github.com/combust/pachyderm-client.git"),
      "scm:git:git@github.com:combust/pachyderm-client.git")),
    developers := List(Developer("hollinwilkins",
      "Hollin Wilkins",
      "hollinrwilkins@gmail.com",
      url("http://hollinwilkins.com")))
  )
}