name := """car-directory-rest-api"""
organization := "com.company"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.10"

libraryDependencies += guice

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
  "com.typesafe.slick" %% "slick" % "2.1.0"
)

libraryDependencies ++= Seq(
  jdbc,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "com.netflix.rxjava" % "rxjava-scala" % "0.20.7"
)

libraryDependencies += "org.webjars" % "jquery" % "2.1.3"

routesGenerator := InjectedRoutesGenerator
