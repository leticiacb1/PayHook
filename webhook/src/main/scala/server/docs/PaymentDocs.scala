package docs

import io.swagger.v3.oas.annotations.media.{Content, Schema}
import model.PaymentPayload

object PaymentDocs {
  final val operationSummary = "Route that will receive a payload representing a payment and check whether it is valid or not"
  final val operationDescription = "Check payment"
  final val bodyDescription = "Payment data"
}