package model

import spray.json.DefaultJsonProtocol

final case class PaymentPayload(transactionId: String,
                                event: String,
                                amount: Double,
                                currency: String,
                                timestamp: String)

object JsonSupport extends DefaultJsonProtocol {
  implicit val paymentFormat: spray.json.RootJsonFormat[PaymentPayload] = jsonFormat5(PaymentPayload)
}
