import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val akkaVersion         = "10.2.10"
val akkaStreamVersion   = "2.6.20"

val akkaHttp            = "com.typesafe.akka" %% "akka-http"             % akkaVersion
val akkaStream          = "com.typesafe.akka" %% "akka-stream"           % akkaStreamVersion
val akkaSprayJson       = "com.typesafe.akka" %% "akka-http-spray-json"  % akkaVersion
val swaggerAkkaHttp     = "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.6.0"
val swaggerAnnotations  = "io.swagger.core.v3" % "swagger-annotations" % "2.2.20"
val jakartaRsApi        = "jakarta.ws.rs" % "jakarta.ws.rs-api"          % "3.1.0"
val munit               = "org.scalameta" %% "munit"                     % "0.7.29"

lazy val root = (project in file("."))
  .settings(
    name := "webhook",
    libraryDependencies ++= Seq(
      akkaHttp,
      akkaStream,
      akkaSprayJson,
      swaggerAkkaHttp,
      swaggerAnnotations,
      jakartaRsApi,
      "com.lihaoyi" %% "scalasql" % "0.1.8",
      "org.xerial" % "sqlite-jdbc" % "3.43.0.0",
      "org.slf4j" % "slf4j-simple" % "2.0.9",
      munit % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
