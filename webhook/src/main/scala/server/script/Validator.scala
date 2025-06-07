package script

import scala.util.matching.Regex

import model.PaymentPayload
import model.PayloadValidation

object PaymentValidator{

  private val acceptedEvents = Set("payment_success")
  private val currencyPattern: Regex = "^[A-Z]{3}$".r
  private val alphanumericPattern: Regex = "^[a-zA-Z0-9]+$".r
  private val minimumAmount : Double = 0.0

  def validate(payload: PaymentPayload): PayloadValidation = {
    val errors : Seq[String] = validateEvent(payload.event) ++
                              validateTransactionId(payload.transaction_id) ++
                              validateAmount(payload.amount) ++
                              validateCurrency(payload.currency) ++
                              validateTimestamp(payload.timestamp)

    PayloadValidation(errors.isEmpty, errors)
  }

  private def validateTransactionId(id: String): Seq[String] =
    if (!alphanumericPattern.matches(id)) Seq(s"Invalid transaction ID: $id") else Seq.empty

  private def validateEvent(event: String): Seq[String] =
    if (!acceptedEvents.contains(event)) Seq(s"Invalid event: $event") else Seq.empty

  private def validateAmount(amount: Double): Seq[String] =
    if (!(amount > minimumAmount)) Seq(s"Amount must be positive: $amount") else Seq.empty

  private def validateCurrency(currency: String): Seq[String] =
    if (currency.trim.length != 3) Seq("Currency must be a 3-letter.") else Seq.empty

  private def validateTimestamp(timestamp: String): Seq[String] =
    if (timestamp.trim.isEmpty) Seq("Timestamp must not be empty.") else Seq.empty
}