package ml.combust.pachyderm

import sbt.Keys._
import sbt._

object Dependencies {
  object Compile {
    val grpc = "io.grpc" % "grpc-netty" % "1.0.3"
    val scalaPbRuntimeRpc = "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % com.trueaccord.scalapb.compiler.Version.scalapbVersion
  }

  object Protobuf {
    val scalaPbRuntime = "com.trueaccord.scalapb" %% "scalapb-runtime" % com.trueaccord.scalapb.compiler.Version.scalapbVersion % "protobuf"
  }

  import Compile._
  val l = libraryDependencies

  lazy val client = l ++= Seq(grpc, scalaPbRuntimeRpc, Protobuf.scalaPbRuntime)
}