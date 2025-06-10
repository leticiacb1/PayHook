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

import storage.Storage

import store.StoreSite
import scala.util.{Success, Failure}

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
      entity(as[PaymentPayload]) { payload =>

        println(s" [INFO] Received payload =  $payload")

        val validation = PaymentValidator.validate(payload)
        println(s" [INFO] Validation =  $validation")

        //val paymentFuture = Storage.get(payload.transaction_id)
        //println(s" [INFO] Payment(transaction_id = ${payload.transaction_id}) in database =  $validation")

        if (validation.isValid) {
          Storage.insert(
            transactionId = payload.transaction_id,
            event = payload.event,
            amount = payload.amount,
            currency = payload.currency,
            timestamp = payload.timestamp
          )
          println(s"\n [INFO] Payment(transaction_id = ${payload.transaction_id}) added to database")

          // Optionally send confirmation here
          StoreSite.post(payload, StoreSite.confirmationRoute)

          complete(StatusCodes.OK, "✅ Payment accepted")
        } else {
          // Optionally send cancellation here
          StoreSite.post(payload, StoreSite.cancellationRoute)

          val errorMessage = validation.errors.take(1).map(err => s"- $err").mkString("\n")
          complete(StatusCodes.BadRequest, s"❌ Invalid payment data : \n$errorMessage")
        }
      }
    }
  }

  def route : Route = check_payment // ~ other_operation
}
