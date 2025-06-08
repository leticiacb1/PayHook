package main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http.ServerBinding

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

import server.Routes
import server.SwaggerDocService

import storage.Storage

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("webhook-system")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  // From resources/
  val swaggerUIRoute : Route = path("docs") { getFromResource("swagger-ui/index.html") }

  // Compose routes
  val allRoutes : Route = new Routes().route ~ SwaggerDocService.routes ~ swaggerUIRoute

  // Create database
  Storage.initialize()

  // Server localhost:8080
  val bindingFuture: Future[ServerBinding] = Http().newServerAt("localhost", 8080).bind(allRoutes)

  println(" ✅ Swagger UI available at http://localhost:8080/docs")
  StdIn.readLine()
  system.terminate()
}
