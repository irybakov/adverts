import com.typesafe.sbt.packager.docker.ExecCmd

enablePlugins(JavaAppPackaging,AshScriptPlugin)

dockerBaseImage := "openjdk:8-jre-alpine"

name := "adverts"

version := "0.1.1"

scalaVersion := "2.13.1"


libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http"   % "10.1.10", 
    "com.typesafe.akka" %% "akka-stream" % "2.5.23",
)