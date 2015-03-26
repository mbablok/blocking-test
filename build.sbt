name := "blockingTest"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")


val akkaV = "2.3.9"
val sprayV = "1.3.3"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV withSources(),
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
)

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"


updateOptions := updateOptions.value.withCachedResolution(true)

