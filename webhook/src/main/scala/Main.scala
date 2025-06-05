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

  val swaggerUIRoute =
    path("docs") {
      getFromResource("swagger-ui/index.html")
    }

  val allRoutes = new Routes().route ~ SwaggerDocService.routes ~ swaggerUIRoute

  Http()
    .newServerAt("localhost", 8080)
    .bind(allRoutes)

  println(" ✅ Swagger UI available at http://localhost:8080/docs")

  StdIn.readLine()
  system.terminate()
}
