package storage

import scalasql.SqliteDialect._
import scalasql.DbClient

import java.nio.file.{Files, Paths}

import org.sqlite.SQLiteDataSource

import model.PaymentTable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Database {
  // Ensure initialize
  private var initialized = false

  // Path to db data storage
  val dbPath = Paths.get("src", "main",  "scala", "server", "data", "payment.db")
  println(s"\n [DATABASE][INFO] Using database file at $dbPath")

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
    println(s"\n [DATABASE][INFO] Table payment_table created ")
  }

  private def ensureInitialized(): Unit = {
    if (!initialized) {
      throw new IllegalStateException("\n [DATABASE][ERROR] Storage not initialized! Call Storage.initialize() before using.")
    }
  }

  def insert(transactionId: String, event: String, amount: String, currency: String, timestamp: String): Int = {
    ensureInitialized()
    sqliteClient.transaction { db =>
      val rowsAffected = db.updateRaw(
        s"""
           INSERT OR IGNORE INTO payment_table (transaction_id, event, amount, currency, timestamp)
           VALUES (?, ?, ?, ?, ?)
        """,
        Seq(transactionId, event, amount, currency, timestamp)
      )
      rowsAffected
    }
  }

  def close(): Unit = {
    println("\n [DATABASE][INFO] Closing database connection.")
    initialized = false
    dataSource.getConnection.close()
  }
}