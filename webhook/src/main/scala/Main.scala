package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContextExecutor

import scala.io.StdIn

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("webhook-system")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val allRoutes = new Routes().route ~ SwaggerDocService.routes

  Http()
    .newServerAt("localhost", 8080)
    .bind(allRoutes)

  println(" ✅ Running on http://localhost:8080")
  println(" Swagger UI available at /api-docs/swagger.json (you'll need to host the UI manually)")
  StdIn.readLine()
  system.terminate()
}
