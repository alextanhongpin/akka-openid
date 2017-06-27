name := "akka-openid"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.8",
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "1.0",
  "com.typesafe.akka" %%"akka-http-testkit-experimental" % "1.0",
  "org.reactivemongo" % "reactivemongo_2.11" % "0.12.4",
  "org.slf4j" % "slf4j-simple" % "1.7.25",
  "org.slf4j" % "slf4j-api" % "1.7.25"
)