import com.typesafe.sbt.packager.docker.ExecCmd

enablePlugins(JavaAppPackaging,AshScriptPlugin)

dockerBaseImage := "openjdk:8-jre-alpine"

name := "adverts"

version := "0.1.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http"   % "10.1.10", 
    "com.typesafe.akka" %% "akka-stream" % "2.5.23",
    "de.heikoseeberger" %% "akka-http-circe" % "1.29.1",
    "io.circe" %% "circe-core" % "0.12.3",
    "io.circe" %% "circe-generic" % "0.12.3",
    "io.circe" %% "circe-parser" % "0.12.3",
    "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)