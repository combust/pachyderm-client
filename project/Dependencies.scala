package ml.combust.pachyderm

import sbt.Keys._
import sbt._

object Dependencies {
  val scalaTestVersion = "3.0.0"

  object Compile {
    val grpc = "io.grpc" % "grpc-netty" % "1.0.3"
    val scalaPbRuntimeRpc = "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % com.trueaccord.scalapb.compiler.Version.scalapbVersion
    val sprayJson = "io.spray" %% "spray-json" % "1.3.3"
  }

  object Test {
    val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  }

  object Protobuf {
    val scalaPbRuntime = "com.trueaccord.scalapb" %% "scalapb-runtime" % com.trueaccord.scalapb.compiler.Version.scalapbVersion % "protobuf"
  }

  import Compile._
  val l = libraryDependencies

  lazy val client = l ++= Seq(grpc, scalaPbRuntimeRpc, Protobuf.scalaPbRuntime, sprayJson, Test.scalaTest)
}