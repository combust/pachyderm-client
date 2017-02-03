package ml.combust.pachyderm

import sbt.Keys._
import sbt._
import sbtprotoc.ProtocPlugin.autoImport.PB

object Protobuf {
  lazy val protobufSettings = Seq(PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value))
}