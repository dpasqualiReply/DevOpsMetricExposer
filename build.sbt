name := "DevOpsMetricExposer"

version := "0.1"

scalaVersion := "2.11.8"

val ScalatraVersion = "2.6.+"
val sparkVersion = "2.2.0"

resolvers ++= Seq(
  "All Spark Repository -> bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/"
)

libraryDependencies ++= Seq(
  "org.scalatra"            %% "scalatra"                     % ScalatraVersion,
  "org.scalatra"            %% "scalatra-scalate"             % ScalatraVersion,
  "org.scalatra"            %% "scalatra-specs2"              % ScalatraVersion     % "test",
  "ch.qos.logback"          % "logback-classic"               % "1.2.3"             % "runtime; compile",
  "org.eclipse.jetty"       % "jetty-webapp"                  % "9.2.15.v20160210"  % "container;compile",
  "javax.servlet"           %  "javax.servlet-api"            % "3.1.0"             % "container;compile",

  "org.scalatra"            %% "scalatra-scalatest"           % "2.6.2"             % "test",

  "org.apache.spark"        %  "spark-core_2.11"              % "2.2.0",
  "org.apache.spark"        %  "spark-sql_2.11"               % "2.2.0",

  "com.typesafe"            %  "config"                       % "1.3.2",
)

libraryDependencies ++= Seq(

  "io.prometheus" % "simpleclient" % "0.1.0",
  "io.prometheus" % "simpleclient_common" % "0.1.0",
  "io.prometheus" % "simpleclient_hotspot" % "0.1.0",
  "io.prometheus" % "simpleclient_pushgateway" % "0.1.0",
)


enablePlugins(JettyPlugin)
//enablePlugins(SbtTwirl)

//containerPort in Jetty := 10000

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

test in assembly := {}