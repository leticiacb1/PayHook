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
  val dbPath = Paths.get("src", "main",  "scala", "server", "data", "payment.db")
  println(s"\n [INFO] Using database file at $dbPath")

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
        db.updateRaw("""DROP TABLE IF EXISTS payment_table;""")
        db.updateRaw("""
          CREATE TABLE payment_table (
            transaction_id VARCHAR(255) PRIMARY KEY,
            event VARCHAR(255),
            amount VARCHAR(255),
            currency VARCHAR(3),
            timestamp VARCHAR(255)
          );""")
    }
    initialized = true
    println(s"\n [INFO] Table payment_table created ")
  }

  private def ensureInitialized(): Unit = {
    if (!initialized) {
      throw new IllegalStateException("❌ Storage not initialized! Call Storage.initialize() before using.")
    }
  }

  def insert(transactionId: String, event: String, amount: String, currency: String, timestamp: String): Unit = {
    ensureInitialized()

    println(s"\n [INFO] SqliteClient = ${sqliteClient} , transactionId = $transactionId, event = $event, amount = $amount, currency = $currency, timestamp = $timestamp")
    sqliteClient.transaction { db =>
      db.updateRaw(
        s"""
           INSERT INTO payment_table (transaction_id, event, amount, currency, timestamp)
           VALUES (?, ?, ?, ?, ?)
        """,
        Seq(transactionId, event, amount, currency, timestamp)
      )
    }
  }

  def get(transactionId: String) = Future {
    ensureInitialized()
    println(s"\n FIZ O GETTT ")
    sqliteClient.transaction {
      db =>
        db.run(PaymentTable.select
                           .filter(_.transaction_id === transactionId)
                           .take(1)
               ).headOption
    }
  }

  def close(): Unit = {
    println("\n [INFO] Closing database connection.")
    initialized = false
    dataSource.getConnection.close()
  }
}