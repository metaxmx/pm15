name := """pm15"""

version := "1.0-SNAPSHOT"

startYear := Some(2015)

description := "Planet Metax 2015 - Private Blog"

scalaVersion := "2.11.7"

developers := List(Developer("metax","Christian Simon","simon@illucit.com",url("http://www.christiansimon.eu")))

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  cache,
  specs2 % Test,
  "com.google.inject" % "guice" % "4.0",
  "com.google.guava" % "guava" % "18.0",
  "javax.inject" % "javax.inject" % "1",
  "joda-time" % "joda-time" % "2.8.1",
  "com.typesafe.slick" %% "slick" % "3.0.2",
  "commons-io" % "commons-io" % "2.4",
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.webjars" % "jquery" % "1.11.3",
  "org.webjars" % "html5shiv" % "3.7.2",
  "org.webjars" % "respond" % "1.4.2",
  "org.mockito" % "mockito-core" % "1.10.17" % "test"
)

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

routesGenerator := play.routes.compiler.InjectedRoutesGenerator

EclipseKeys.preTasks := Seq(compile in Compile)

EclipseKeys.withSource := true

