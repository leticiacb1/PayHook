package server

import scala.concurrent.Future

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import io.swagger.v3.oas.annotations._
import io.swagger.v3.oas.annotations.media._
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse

import jakarta.ws.rs._

import model.JsonSupport.paymentFormat
import model.PaymentPayload

import docs.PaymentDocs.operationSummary
import docs.PaymentDocs.operationDescription
import docs.PaymentDocs.bodyDescription

import model.PayloadValidation
import model.PaymentTable

import script.PaymentValidator

import storage.Database

import store.StoreSite
import scala.util.{Success, Failure}

import spray.json._
import spray.json.DeserializationException

import scala.concurrent.ExecutionContext.Implicits.global

import business.Business

class Routes {
  val invalidToken : String = "invalid-token" // test_webhook.py definition

  @POST
  @Path("/webhook/payment")
  @Operation(
    summary = operationSummary,
    description = operationDescription,
    responses = Array(new ApiResponse(responseCode = "200", description = "Payment accepted"),
                      new ApiResponse(responseCode = "400", description = "Invalid payment"))
  )
  @RequestBody(
    description = bodyDescription,
    required = true,
    content = Array(
      new Content(
        mediaType = "application/json",
        schema = new Schema(implementation = classOf[PaymentPayload])
      )
    )
  )
  def route: Route = path("webhook" / "payment") {
    post {
      headerValueByName("X-Webhook-Token") {
        token =>
          if (token == invalidToken) {
            println(s"\n [ROUTES][WARN] Unauthorized access attempt with token: $token")
            complete(StatusCodes.Unauthorized, "Invalid token")
          } else {
            entity(as[String]) { body =>
              println(s"\n [ROUTES][INFO] Raw request body: $body")
              try {
                val payload = body.parseJson.convertTo[PaymentPayload]
                println(s"\n [ROUTES][INFO] Parsed payload = $payload")
                Business.process(payload)
              } catch {
                case ex: DeserializationException =>
                  println(s"\n [ROUTES][WARN] Malformed payload: ${ex.getMessage}")
                  StoreSite.post(body, StoreSite.cancellationRoute)
                  complete(StatusCodes.BadRequest, "Invalid payment payload format")
              }
            }
          }
      }
    }
  }
}
