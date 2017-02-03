logLevel := Level.Warn

addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.3")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.5.47"
