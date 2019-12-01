import com.typesafe.sbt.packager.docker.ExecCmd

enablePlugins(JavaAppPackaging, AshScriptPlugin)

dockerBaseImage := "openjdk:8-jre-alpine"

name := "adverts"

version := "0.1.1"

scalaVersion := "2.13.1"

val akkaHttpVersion = "10.1.10"
val akkaVersion = "2.6.0"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,

    "de.heikoseeberger" %% "akka-http-json4s" % "1.29.1",
    "org.json4s" %% "json4s-native" % "3.6.7",
    "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)