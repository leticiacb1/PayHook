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
        entity(as[String]) {
          body =>
            println(s" [INFO] Raw request body: $body")

            try {
              val payload = body.parseJson.convertTo[PaymentPayload]

              println(s" [INFO] Parsed payload =  $payload")

                val validation = PaymentValidator.validate(payload);
                println(s" [INFO] Validation =  $validation");

                val rowsAffected = Database.insert(
                  transactionId = payload.transaction_id,
                  event = payload.event,
                  amount = payload.amount,
                  currency = payload.currency,
                  timestamp = payload.timestamp
                );

                if (validation.isValid && rowsAffected == 1) {
                  Database.insert(
                    transactionId = payload.transaction_id,
                    event = payload.event,
                    amount = payload.amount,
                    currency = payload.currency,
                    timestamp = payload.timestamp
                  );
                  println(s"\n [INFO] Payment(transaction_id = ${payload.transaction_id}) added to database");

                  // Optionally send confirmation here
                  StoreSite.post(payload, StoreSite.confirmationRoute);

                  complete(StatusCodes.OK, "✅ Payment accepted");
                } else {
                  var errorMessage: String = ""
                  if (validation.isValid) {
                    errorMessage = s"❌ Payment(transaction_id = ${payload.transaction_id}) already exists in database";
                    println(s"\n [ERROR] $errorMessage");
                  } else {
                    val errors: String = validation.errors.take(1).map(err => s"- $err").mkString("\n");
                    errorMessage = s"❌ Invalid payment data : \n$errorMessage";
                    println(s"\n [ERROR] $errorMessage");
                  }

                  // Optionally send cancellation here
                  StoreSite.post(payload, StoreSite.cancellationRoute);

                  complete(StatusCodes.BadRequest, errorMessage);
                }

            } catch {
              case ex: DeserializationException =>
                println(s" [WARN] ❌ Malformed payload: ${ex.getMessage}")
                StoreSite.postWrongBody(body)
                // ✅ Explicit return
                complete(StatusCodes.BadRequest, "❌ Invalid payment payload format")
            }
      }
    }
  }

  def route : Route = check_payment // ~ other_operation
}
