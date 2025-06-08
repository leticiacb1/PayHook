package store

import io.circe._, io.circe.parser._, io.circe.generic.auto._
import sttp.client3._
import sttp.client3.circe._
import sttp.model._

import model.PaymentPayload

object StoreSite {

  val url: String = "http://127.0.0.1:5001"           // test_webhook.py definition
  val confirmation_route: String = "/confirmar"
  val cancellation_route: String = "/cancelar"

  def post(payload: PaymentPayload, route: String): Unit = {
    val backend = HttpURLConnectionBackend()

    val request = basicRequest
      .post(uri"$url$route")
      .body(payload)
      .contentType("application/json")
      .response(asJson[Json])             // expects JSON back

    val response = request.send(backend)

    response.body match {
      case Right(json) =>
        val status = json.hcursor.get[String]("status")
        println(s"\n [INFO] StoreSite response status: $status")

      case Left(error) =>
        println(s"\n [ERROR] Failed to parse JSON response: $error")
    }
  }
}
