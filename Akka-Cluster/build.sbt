name := "Akka-Cluster"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.4",
  "com.typesafe.akka" %% "akka-remote" % "2.5.4",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.3",
  "com.typesafe.akka" %% "akka-contrib" % "2.5.4",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.4",
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "com.typesafe.akka" % "akka-cluster-metrics_2.12" % "2.5.6",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
)