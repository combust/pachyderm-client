package ml.combust.pachyderm

import sbt._
import sbt.Keys._

object Modules {
  lazy val aggregatedProjects: Seq[ProjectReference] = Seq(
    `pachyderm-client`
  )

  lazy val rootSettings = Common.defaultSettings
  lazy val `root` = Project(
    id = "pachyderm-client-root",
    base = file("."),
    aggregate = aggregatedProjects
  ).settings(rootSettings)

  lazy val `pachyderm-client` = Project(
    id = "pachyderm-client",
    base = file("pachyderm-client")
  )

  lazy val `pachyderm-client-akka` = Project(
    id = "pachyderm-client-akka",
    base = file("pachyderm-client-akka")
  )
}