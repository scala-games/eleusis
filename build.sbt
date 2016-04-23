name := "eleusis"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache,
  ws		
)

libraryDependencies += "com.propensive" %% "rapture-json-jawn" % "1.1.0"

includeFilter in (Assets, LessKeys.less) := "cards.less" | "cards.less"