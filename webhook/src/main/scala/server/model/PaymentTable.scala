package model

import scalasql.Table

case class PaymentTable[T[_]](
                                  transaction_id: T[String],
                                  event: T[String],
                                  amount: T[Double],
                                  currency: T[String],
                                  timestamp: T[String]
                                )

// Schema
object PaymentTable extends Table[PaymentTable]