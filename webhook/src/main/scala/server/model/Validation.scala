package model

case class PayloadValidation(isValid: Boolean, errors: Seq[String])