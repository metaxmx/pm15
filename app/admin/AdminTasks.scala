package admin

import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import models.StaticPages
import models.BlogEntries
import play.api.db.slick.DatabaseConfigProvider
import play.api.Application
import play.api.Play
import slick.driver.JdbcProfile

/**
 * Admin tasks.
 */
object AdminTasks {

  def main(args: Array[String]) = args.toList match {
    case "schemify" :: Nil => schemify
    case _                 => help
  }

  def help = {
    println("""|Admin Tasks:
               | - schemify : Create Database Schema (drop existing data!)
               |""".stripMargin)
  }

  def schemify = {
    println("Creating Database Schema ...")
    withDb {
      db =>
        val schema = StaticPages.schema ++ BlogEntries.schema
        schema.createStatements.foreach { println(_) }
        db.run(DBIO.seq(
          schema.drop,
          schema.create))
    }
  }

  def withDb(block: Database => Unit) = {
    val db = Database.forConfig("slick.dbs.default")
    try {
      block(db)
    } finally db.close
  }

}