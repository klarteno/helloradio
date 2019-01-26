organization in ThisBuild := "ngcp.com.iqa"
version in ThisBuild := "1.0-SNAPSHOT"


import scalapb.compiler.Version.scalapbVersion


// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.8"

//PB.protoSources in Compile := Seq(file("helloradio-impl/src/main/protobuf"))


PB.protoSources in Compile := Seq((baseDirectory in ThisBuild).value / "helloradio-impl"/ "src"/ "main" / "protobuf")


PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

libraryDependencies ++= Seq(
  // For finding google/protobuf/descriptor.proto
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapbVersion % "protobuf"
)


val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test



lazy val `helloradio` = (project in file("."))
  .aggregate(`helloradio-api`, `helloradio-impl`)

lazy val `helloradio-api` = (project in file("helloradio-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
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
