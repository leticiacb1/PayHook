package storage

import scalasql.SqliteDialect._
import scalasql.DbClient

import java.nio.file.{Files, Paths}

import org.sqlite.SQLiteDataSource

import model.PaymentTable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Storage {
  // Ensure initialize
  private var initialized = false

  // Path to db data storage
  val dbPath = Paths.get("scala", "server", "data", "payment.db")

  // SQLite JDBC data source object
  val dataSource = new SQLiteDataSource()
  dataSource.setUrl(s"jdbc:sqlite:$dbPath")

  // SQL Client
  lazy val sqliteClient = new scalasql.DbClient.DataSource(
    dataSource,
    config = new scalasql.Config {}
  )

  def initialize(): Unit = {
    sqliteClient.transaction {
      db =>
        db.updateRaw("""
          CREATE TABLE payment (
            transaction_id VARCHAR(255) PRIMARY KEY,
            event VARCHAR(255),
            amount DOUBLE,
            currency VARCHAR(3),
            timestamp VARCHAR(255)
          );""")
    }
    initialized = true
  }

  private def ensureInitialized(): Unit = {
    if (!initialized) {
      throw new IllegalStateException("❌ Storage not initialized! Call Storage.initialize() before using.")
    }
  }

  def insert(transactionId: String, event: String, amount: Double, currency: String, timestamp: String): Unit = {
    ensureInitialized()
    sqliteClient.transaction { db =>
      db.run(
        PaymentTable.insert.batched(_.transaction_id, _.event, _.amount, _.currency, _.timestamp)(
          (transactionId, event, amount, currency, timestamp)
        )
      )
    }
  }

  def get(transactionId: String) = {
    ensureInitialized()
    sqliteClient.transaction {
      db =>
        db.run(PaymentTable.select
                           .filter(_.transaction_id === transactionId)
                           .take(1)
               ).headOption
    }
  }
}