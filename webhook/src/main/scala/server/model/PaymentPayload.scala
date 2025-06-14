package model

import spray.json.DefaultJsonProtocol

final case class PaymentPayload(transaction_id: String,
                                event: String,
                                amount: String,
                                currency: String,
                                timestamp: String)

object JsonSupport extends DefaultJsonProtocol {
  implicit val paymentFormat: spray.json.RootJsonFormat[PaymentPayload] = jsonFormat5(PaymentPayload)
}
