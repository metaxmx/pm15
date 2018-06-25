name := """pm15"""

version := "1.1.1"

startYear := Some(2015)

description := "Planet Metax 2015 - Private Blog"

scalaVersion := "2.11.7"

developers := List(Developer("metax","Christian Simon","simon@illucit.com",url("https://www.planet-metax.de")))

lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val admin = inputKey[Unit]("Admin Tasks")

fullRunInputTask(admin, Compile, "admin.AdminTasks")

resolvers ++= Seq(
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "scalaz-bintray"     at "http://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  cache,
  "com.google.inject"     %  "guice"                 % "4.0"             withSources() withJavadoc,
  "com.google.guava"      %  "guava"                 % "19.0"            withSources() withJavadoc,
  "javax.inject"          %  "javax.inject"          % "1"               withSources() withJavadoc,
  "joda-time"             %  "joda-time"             % "2.9.2"           withSources() withJavadoc,
  "org.joda"              %  "joda-convert"          % "1.8.1"           withSources() withJavadoc,
  "com.typesafe.slick"    %% "slick"                 % "3.1.0"           withSources() withJavadoc,
  "com.github.tototoshi"  %% "slick-joda-mapper"     % "2.0.0"           withSources() withJavadoc,
  "com.typesafe.play"     %% "play-slick"            % "1.1.1"           withSources() withJavadoc,
  "com.typesafe.play"     %% "play-slick-evolutions" % "1.1.1"           withSources() withJavadoc,
  "mysql"                 %  "mysql-connector-java"  % "5.1.38"                                   ,
  "commons-io"            %  "commons-io"            % "2.4"             withSources() withJavadoc,
  "org.jsoup"             %  "jsoup"                 % "1.8.3"           withSources() withJavadoc,
  "org.pegdown"           %  "pegdown"               % "1.6.0"           withSources() withJavadoc,
  "org.python"            %  "jython-standalone"     % "2.7.0"           withSources() withJavadoc,
  "org.pygments"          %  "pygments"              % "2.0.2"                                    ,
  "com.sksamuel.scrimage" %% "scrimage-core"         % "2.1.1"           withSources() withJavadoc,
  "org.webjars"           %  "bootstrap"             % "3.3.6"                                    ,
  "org.webjars"           %  "jquery"                % "1.11.3"                                   ,
  "org.webjars"           %  "html5shiv"             % "3.7.3"                                    ,
  "org.webjars"           %  "respond"               % "1.4.2"                                    ,
  "org.webjars"           %  "ace"                   % "1.2.2"                                    ,
  specs2                                                         % Test  withSources() withJavadoc,
  "org.mockito"           %  "mockito-core"          % "1.10.19" % Test  withSources() withJavadoc
)

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

routesGenerator := play.routes.compiler.InjectedRoutesGenerator

EclipseKeys.preTasks := Seq(compile in Compile, compile in Test)

EclipseKeys.withSource := true

fork in run := false
