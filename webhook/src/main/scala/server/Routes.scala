package server

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import io.swagger.v3.oas.annotations._
import io.swagger.v3.oas.annotations.media._
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs._

import spray.json.DefaultJsonProtocol

final case class HelloPayload(message: String)

object JsonSupport extends DefaultJsonProtocol {
  implicit val helloFormat: spray.json.RootJsonFormat[HelloPayload] = jsonFormat1(HelloPayload)
}

@Path("/hello")
class Routes {

  import JsonSupport._

  @POST
  @Operation(
    summary = "Say hello",
    description = "Returns the string",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "OK")
    )
  )
  @RequestBody(
    description = "A test request body",
    required = true,
    content = Array(
      new Content(
        mediaType = "application/json",
        schema = new Schema(implementation = classOf[HelloPayload])
      )
    )
  )
  def route: Route = path("hello") {
    post {
      entity(as[HelloPayload]) { payload =>
        complete(s"Received: ${payload.message}")
      }
    }
  }
}
