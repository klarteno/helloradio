organization in ThisBuild := "ngcp.com.iqa"
version in ThisBuild := "1.0-SNAPSHOT"

import scalapb.compiler.Version.scalapbVersion

scalaVersion in ThisBuild := "2.12.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `helloradio` = (project in file("."))
  .aggregate(`helloradio-api`, `helloradio-impl`)

lazy val `helloradio-api` = (project in file("helloradio-api"))
  .settings(
    name := "helloradio-api",
    libraryDependencies ++= Seq(
      "com.thesamet.test" % "test-protos" % "0.1" % "protobuf",
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapbVersion % "protobuf",
      lagomScaladslApi
    ),
    // Dependencies marked with "protobuf" get extracted to target / protobuf_external
    // In addition to the JAR we care about, the protobuf_external directory
    // is going to contain protos from ScalaPB runtime and Google's standard
    // protos.  In order to avoid compiling them, we restrict what's compiled
    // to a subdirectory of protobuf_external
    PB.protoSources in Compile += target.value / "protobuf_external" / "com" / "thesamet",
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    )
  )

lazy val `helloradio-impl` = (project in file("helloradio-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`helloradio-api`)
