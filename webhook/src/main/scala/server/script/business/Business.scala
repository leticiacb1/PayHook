package business

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import model.PayloadValidation
import model.PaymentTable
import model.PaymentPayload

import script.PaymentValidator

import storage.Database

import store.StoreSite

object Business {

  def process(payload: PaymentPayload): Route = {
    val validation = PaymentValidator.validate(payload);
    println(s"\n [BUSINESS][INFO] Validation =  $validation");

    val rowsAffected = Database.insert(
      transactionId = payload.transaction_id,
      event = payload.event,
      amount = payload.amount,
      currency = payload.currency,
      timestamp = payload.timestamp
    );

    if (validation.isValid && rowsAffected == 1) {
      confirm_payment(payload)
      complete(StatusCodes.OK, "Payment accepted");
    } else {
      val errorMessage : String = cancel_payment(payload, validation)
      complete(StatusCodes.BadRequest, errorMessage);
    }
  }

  private  def confirm_payment(payload: PaymentPayload): Unit = {
    println(s"\n [BUSINESS][INFO] Payment(transaction_id = ${payload.transaction_id}) added to database");

    // Optionally send confirmation
    StoreSite.post(payload, StoreSite.confirmationRoute);
  }

  private def cancel_payment(payload: PaymentPayload, validation: PayloadValidation): String = {
    var errorMessage: String = ""
    if (validation.isValid) {
      errorMessage = s"Payment(transaction_id = ${payload.transaction_id}) already exists in database";
    } else {
      val errors: String = validation.errors.take(1).map(err => s"- $err").mkString("\n");
      errorMessage = s"Invalid payment data : \n$errorMessage";
    }
    println(s"\n [BUSINESS][ERROR] $errorMessage");

    // Optionally send cancellation
    StoreSite.post(payload, StoreSite.cancellationRoute);

    errorMessage
  }
}