import sbt._

object Dependencies {
  val akkaHttp           = "com.typesafe.akka" %% "akka-http" % "10.5.2"
  val akkaStream         = "com.typesafe.akka" %% "akka-stream" % "2.8.5"
  val akkaSprayJson      = "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.2"
  val swaggerAkkaHttp    = "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.6.0"
  val swaggerAnnotations = "io.swagger.core.v3" % "swagger-annotations" % "2.2.20"
  val jakartaRsApi       = "jakarta.ws.rs" % "jakarta.ws.rs-api" % "3.1.0"

  lazy val munit = "org.scalameta" %% "munit" % "0.7.29"
}
