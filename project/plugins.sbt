
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// The Play plugin
//addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.3")
addSbtPlugin("org.scalatra.sbt" % "sbt-scalatra" % "1.0.0")