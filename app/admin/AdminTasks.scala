package admin

import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import models._
import play.api.db.slick.DatabaseConfigProvider
import play.api.Application
import play.api.Play
import slick.driver.JdbcProfile
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import play.api.db.slick.NamedDatabaseConfigProvider
import slick.driver.MySQLDriver
import play.api.db.slick.DefaultSlickApi
import slick.backend.DatabaseConfig
import slick.profile.BasicProfile
import com.typesafe.config.ConfigFactory
import util.Logging
import play.api.Logger
import java.io.File
import play.api.Mode
import util.renderers.ContentRenderers
import scala.util.Failure
import scala.util.Success
import util.renderers.ContentWithAbstract

/**
 * Admin tasks.
 */
object AdminTasks extends Logging {

  val number = "([0-9]+)".r

  def main(args: Array[String]) = {
    Logger.init(new File("."), Mode.Dev)
    args.toList match {
      case "schemify" :: Nil                         => schemify
      case "render" :: Nil                           => render(true, true, None)
      case "render" :: "blog" :: Nil                 => render(true, false, None)
      case "render" :: "static" :: Nil               => render(false, true, None)
      case "render" :: "blog" :: number(id) :: Nil   => render(true, false, Some(id.toInt))
      case "render" :: "static" :: number(id) :: Nil => render(false, true, Some(id.toInt))
      case _                                         => help
    }
  }

  def help = {
    println("""|Admin Tasks:
               | - schemify           : Create Database Schema (drop existing data!)
               | - render             : Render all blog entries and static pages
               | - render blog        : Render all blog entries
               | - render static      : Render all static pages
               | - render blog <id>   : Render only blog entry <id>
               | - render static <id> : Render only static page <id>
               |""".stripMargin)
  }

  def schemify = {
    log.info("Creating Database Schema ...")
    withDb {
      db =>
        val schema = StaticPages.schema ++
          BlogEntries.schema ++
          Categories.schema ++
          Attachments.schema ++
          Tags.schema ++
          BlogEntryHasTags.schema
        schema.createStatements.foreach { println(_) }
        db.run(DBIO.seq(
          schema.drop,
          schema.create))
    }
  }

  def render(renderBlog: Boolean, renderStatic: Boolean, id: Option[Int]) = {
    withDb {
      db =>
        if (renderBlog) {
          log.info("Rerender " + id.map("Blog Entry " + _).getOrElse("All Blog Entries") + " ...")
          val blogsQuery = for {
            blog <- BlogEntries
          } yield blog
          val blogsFuture = db.run(blogsQuery.result.map { _ filter { blog => id.forall { _ == blog.id } } })
          val blogs = Await.result(blogsFuture, Duration.Inf)
          blogs.foreach {
            blog =>
              log.info(s"Render Blog ${blog.id}")
              ContentRenderers.render(blog.content, blog.contentFormat) match {
                case None             => log.warn(s"Content Format ${blog.contentFormat} in blog entry ${blog.id} not defined.")
                case Some(Failure(e)) => log.error(s"Error during rendering of blog entry ${blog.id}", e)
                case Some(Success(ContentWithAbstract(abstr, content))) => {
                  val updadeQuery = for { b <- BlogEntries if b.id === blog.id } yield (b.abstractRendered, b.contentRendered)
                  val updateAction = updadeQuery.update(abstr, content)
                  Await.result(db.run(updateAction), Duration.Inf)
                }
              }
          }
        }
        if (renderStatic) {
          log.info("Rerender " + id.map("Static Page " + _).getOrElse("All Static Pages") + " ...")
        }
    }
  }

  lazy val config = ConfigFactory.defaultApplication()

  def getDb = {
    DatabaseConfig.forConfig[MySQLDriver](path = "slick.dbs.default", config = config)
  }

  def withDb(block: Database => Unit) = {
    val db = getDb.db
    try {
      block(db)
    } finally db.close
  }

}