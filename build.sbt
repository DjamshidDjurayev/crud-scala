name := """time-spot"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"
herokuJdkVersion in Compile := "1.8"
herokuAppName in Compile := "time-spot"

libraryDependencies ++= Seq(
  jdbc,
  "org.postgresql" % "postgresql" % "9.4-1205-jdbc42",
  "org.sorm-framework" % "sorm" % "0.3.19",
  "org.webjars" % "jquery" % "2.1.4",
  "org.webjars" % "bootstrap" % "3.3.5",
  "com.google.code.gson" % "gson" % "2.4",
  "com.github.nscala-time" %% "nscala-time" % "2.12.0",
  cache,
  ws,
  specs2 % Test
)


dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.

routesGenerator := InjectedRoutesGenerator
routesGenerator := StaticRoutesGenerator
