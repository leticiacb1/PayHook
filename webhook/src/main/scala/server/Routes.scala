package server

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

import script.PaymentValidator

class Routes {
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
  def check_payment: Route = path("webhook" / "payment") {
    post {
      entity(as[PaymentPayload]) {
        payload => {
          val validation = PaymentValidator.validate(payload)

          if(validation.isValid)
            complete(StatusCodes.OK, "✅ Payment accepted")
          else
            complete(StatusCodes.BadRequest, s"❌ Invalid payment data : ${validation.errors}")
        }
      }
    }
  }

  def route : Route = check_payment // ~ other_operation
}
