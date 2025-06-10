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
    val backend = HttpURLConnectionBackend()

    val jsonBody: String = payload.toJson.compactPrint
    val fullUrl: String = s"$url$route"

    val request = basicRequest
      .post(uri"$fullUrl")
      .body(jsonBody)
      .contentType("application/json")
      .response(asStringAlways)

    val response = request.send(backend)

    println(s"[DEBUG] Status code: ${response.code}")
    println(s"[DEBUG] Raw response body: ${response.body}")

    if(response.code == StatusCode.Ok) {
      println(s"[INFO] Successfully sent payload to $fullUrl")
    } else {
      println(s"[ERROR] Failed to send payload to $fullUrl: ${response.body}")
    }
  }

  def postWrongBody(payload: String): Unit = {
    val backend = HttpURLConnectionBackend()

    // val jsonBody: String = payload.toJson.compactPrint
    val fullUrl: String = s"$url$cancellationRoute"
    println(s"[DEBUG] Wrong body: $payload")

    val request = basicRequest
      .post(uri"$fullUrl")
      .body(payload)
      .contentType("application/json")
      .response(asStringAlways)

    val response = request.send(backend)

    println(s"[DEBUG] Status code: ${response.code}")
    println(s"[DEBUG] Raw response body: ${response.body}")
  }

}


