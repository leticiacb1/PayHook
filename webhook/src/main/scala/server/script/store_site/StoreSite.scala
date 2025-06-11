package store

import io.circe._, io.circe.parser._, io.circe.generic.auto._
import sttp.client3._
import sttp.client3.circe._
import sttp.model._

import spray.json._
import model.{PaymentPayload, JsonSupport}
import JsonSupport._

object StoreSite {

  val url: String = "http://localhost:5001"           // test_webhook.py definition
  val confirmationRoute: String = "/confirmar"
  val cancellationRoute: String = "/cancelar"

  def post(payload: PaymentPayload, route: String): Unit = {
    val jsonBody = payload.toJson.compactPrint
    sendPost(jsonBody, route)
  }

  def post(payload: String, route: String): Unit = {
    sendPost(payload, route)
  }

  private def sendPost(jsonBody: String, route: String): Unit = {
    val backend = HttpURLConnectionBackend()
    val fullUrl = s"$url$route"

    val request = basicRequest
      .post(uri"$fullUrl")
      .body(jsonBody)
      .contentType("application/json")
      .response(asStringAlways)

    val response = request.send(backend)
    if(response.code == StatusCode.Ok) {
      println(s"\n [STORE SITE][INFO] Successfully sent to $fullUrl")
    } else {
      println(s"\n [STORE SITE][ERROR] Failed to send to $fullUrl")
    }

  }
}


