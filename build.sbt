name := "play-java-soap"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.apache.cxf" % "cxf-rt-frontend-jaxws" % "3.1.6",
  "org.apache.cxf" % "cxf-rt-transports-http" % "3.1.6"
)
