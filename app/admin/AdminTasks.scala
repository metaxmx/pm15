package admin

import java.io.{ File, FileNotFoundException }
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success, Try }
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import com.typesafe.config.ConfigFactory
import play.api.{ Logger, Mode }
import play.api.libs.json.Json
import models._
import slick.backend.DatabaseConfig
import slick.driver.MySQLDriver
import slick.driver.MySQLDriver.api._
import util.Logging
import util.renderers.{ ContentRenderers, ContentWithAbstract, MarkdownContentRenderer }
import org.apache.commons.io.FilenameUtils
import util.renderers.RenderContext
import util.renderers.RenderTypeBlog
import controllers.routes

/**
 * Admin tasks.
 */
object AdminTasks extends Logging {

  val importFolderBlog = "import/blog"
  val importFolderStatic = "import/static"

  val intId = "([0-9]+)".r

  val mdFormat = MarkdownContentRenderer.renderFormat

  lazy val config = ConfigFactory.defaultApplication()

  def dbConfig = DatabaseConfig.forConfig[MySQLDriver](path = "slick.dbs.default", config = config)

  def main(args: Array[String]) = {
    Logger.init(new File("."), Mode.Dev)
    args.toList match {
      case "reset" :: Nil               => reset
      case "clear" :: Nil               => clear
      case "render" :: Nil              => render(None)
      case "render" :: intId(id) :: Nil => render(Some(id.toInt))
      case "import" :: Nil              => importBlog()
      case "import" :: intId(id) :: Nil => importBlog(Some(id.toInt))
      case _                            => help
    }
  }

  def help = {
    println("""|Admin Tasks:
               | - reset          : Run clear, then import
               | - clear          : Clear all, ready for import
               | - render         : Render all blog entries
               | - render <id>    : Render only blog entry <id>
               | - import         : Import all blog entries
               | - import <id>    : Import blog entry with id <id>
               |""".stripMargin)
  }

  def reset = {
    clear
    importBlog()
  }

  def clear = {
    withDb {
      db =>
        val deletes = Seq(
          Attachments.delete,
          BlogEntryHasTags.delete,
          BlogEntries.delete,
          Categories.delete,
          Tags.delete)
        println(deletes.flatMap(_.statements).mkString("\nStatements:\n", ";\n", ";\n"))
        val deleteAction = DBIO.sequence(deletes)
        Await.result(db.run(deleteAction), Duration.Inf)
    }
  }

  def render(id: Option[Int]) = {
    withDb {
      db =>
        log.info("Rerender " + id.map("Blog Entry " + _).getOrElse("All Blog Entries") + " ...")
        val blogsQuery = for {
          blog <- BlogEntries
        } yield blog
        val blogsFuture = db.run(blogsQuery.result.map { _ filter { blog => id.forall { _ == blog.id } } })
        val blogs = Await.result(blogsFuture, Duration.Inf)
        blogs.foreach {
          blog =>
            log.info(s"Render Blog ${blog.id}")
            val attachmentsFolder = Option(new File(s"media/blog/${blog.id}")) filter { _.isDirectory }
            implicit val context = RenderContext(RenderTypeBlog, blog.contentFormat, attachmentsFolder,
                routes.BlogController.showBlogEntry(blog.url), routes.BlogController.attachment(blog.url, _))
            ContentRenderers.render(blog.content) match {
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
  }

  case class BlogMeta(title: String, category: String, tags: Seq[String], published: Boolean, publishDate: Option[String]) {

    val dateTimePattern = "yy-MM-dd HH:mm:ss";

    def getPublishDateAsDatetime: Option[DateTime] =
      publishDate map { DateTime.parse(_, DateTimeFormat.forPattern(dateTimePattern)) }

  }

  implicit val blogMetaFormat = Json.format[BlogMeta]

  def importBlog(id: Option[Int] = None): Unit = id match {
    case None => {
      val importBlogFolder = Option(new File(importFolderBlog)).filter(_.isDirectory)
      importBlogFolder.foreach {
        _.listFiles.filter(_.getName.matches("[0-9]+")).foreach { f => importBlog(Some(f.getName.toInt)) }
      }
    }
    case Some(id) => {
      val importData = for {
        blogFolder <- checkFile(new File(s"$importFolderBlog/$id"))
        contentFile <- checkFile(new File(blogFolder, "content.md"))
        metaFile <- checkFile(new File(blogFolder, "meta.json"))
        attachmentsFolder <- Success(Option(new File(blogFolder, "attachments")) filter { _.exists })
      } yield (blogFolder, contentFile, metaFile, attachmentsFolder)
      importData match {
        case Failure(e) => log.error(s"Error finding file ${e.getMessage}")
        case Success((blogFolder, contentFile, metaFile, attachmentsFolder)) => {
          val metaData = FileUtils.readFileToString(metaFile, "UTF-8")
          val contentData = FileUtils.readFileToString(contentFile, "UTF-8")
          val metaJson = Json.parse(metaData)
          metaJson.asOpt[BlogMeta] match {
            case None       => log.error(s"Error parsing $metaFile")
            case Some(meta) => importBlogEntry(id, meta, contentData, attachmentsFolder)
          }
        }
      }
    }
  }

  private def importBlogEntry(id: Int, meta: BlogMeta, content: String, attachmentsFolder: Option[File]) = {
    log.info(s"Import blog entry ${meta.title} (id: ${id})")
    withDb {
      db =>

        def getOrCreateCategory(title: String): Int = {
          val categoryExistingQuery = db.run(Categories.filter(_.title === title).map { _.id }.result.headOption)
          val categoryFuture = categoryExistingQuery flatMap {
            _ match {
              case Some(categoryExisting) => {
                log.debug(s"Already found Category '$title': $categoryExisting")
                Future.successful(categoryExisting)
              }
              case None => {
                log.info(s"Inserted Category '$title'")
                db.run((Categories returning Categories.map { _.id }) += Category(0, mkUrl(title), title))
              }
            }
          }
          Await.result(categoryFuture, Duration.Inf)
        }

        def getOrCreateTag(title: String): Int = {
          val tagExistingQuery = db.run(Tags.filter(_.title === title).map { _.id }.result.headOption)
          val tagFuture = tagExistingQuery flatMap {
            _ match {
              case Some(tagExisting) => {
                log.debug(s"Already found Tag '$title': $tagExisting")
                Future.successful(tagExisting)
              }
              case None => {
                log.info(s"Inserted Tag '$title'")
                db.run((Tags returning Tags.map { _.id }) += Tag(0, mkUrl(title), title))
              }
            }
          }
          Await.result(tagFuture, Duration.Inf)
        }

        case class BlogInsertResult(id: Int, inserted: Boolean)

        def getOrInsertBlogEntry(blogEntry: BlogEntry): BlogInsertResult = {
          val entryExistingQuery = db.run(BlogEntries.filter(_.url === blogEntry.url).map { _.id }.result.headOption)
          val blogEntryFuture = entryExistingQuery flatMap {
            _ match {
              case Some(blogEntryExisting) => {
                log.debug(s"Already found Blog Entry '${blogEntry.url}': $blogEntryExisting")
                Future.successful(BlogInsertResult(blogEntryExisting, false))
              }
              case None => {
                log.info(s"Inserted Blog Entry '${blogEntry.url}'")
                db.run((BlogEntries returning BlogEntries.map { _.id }) += blogEntry) map (BlogInsertResult(_, true))
              }
            }
          }
          Await.result(blogEntryFuture, Duration.Inf)
        }

        val categoryId = getOrCreateCategory(meta.category)
        val tagIds = meta.tags.map { getOrCreateTag(_) }
        val url = mkUrl(meta.title)

        implicit val context = RenderContext(RenderTypeBlog, mdFormat, attachmentsFolder,
            routes.BlogController.showBlogEntry(url), routes.BlogController.attachment(url, _))
        val ContentWithAbstract(abstractRendered, contentRendered) = ContentRenderers.render(content) match {
          case None => {
            log.error(s"Content Format ${mdFormat} in blog entry ${id} not defined.")
            ContentWithAbstract("error", "error")
          }
          case Some(Failure(e)) => {
            log.error(s"Error during rendering of blog entry ${id}", e)
            ContentWithAbstract("error", "error")
          }
          case Some(Success(contentWithAbstract)) => contentWithAbstract
        }
        val blogEntry = BlogEntry(0, categoryId, url, meta.title, content,
          contentRendered, abstractRendered, mdFormat, meta.published, meta.getPublishDateAsDatetime, 0)

        val BlogInsertResult(blogId, blogInserted) = getOrInsertBlogEntry(blogEntry)

        if (blogInserted) {

          // Add tags
          val tagInserts = tagIds.map { tagId => BlogEntryHasTags += BlogEntryHasTag(0, blogId, tagId) }
          Await.result(db.run(DBIO.sequence(tagInserts)), Duration.Inf)

          // Attachments
          attachmentsFolder foreach {
            _.listFiles().foreach {
              file =>
                val filename = file.getName
                val url = mkUrl(filename)
                val filenameOpt = if (filename == url) None else Some(filename)
                log.info(s"Add Attachment $filename")
                val mediaFile = new File(s"media/blog/$blogId/$filename")
                val extension = FilenameUtils.getExtension(filename)
                val mime = mimes.get(extension).getOrElse(throw new IllegalArgumentException(s"Unknown MIME type for extension $extension"))
                FileUtils.copyFile(file, mediaFile)
                val attachment = Attachment(0, blogId, mkUrl(filename), filenameOpt, AttachmentTypes.InlineAttachment, mime, 0)
                Await.result(db.run(Attachments += attachment), Duration.Inf)
            }
          }

        }
    }
  }

  val mimes = Map(
    "png" -> "image/png",
    "jpg" -> "image/jpeg",
    "gif" -> "image/gif",
    "java" -> "text/x-java-source")

  private def mkUrl(title: String) = {
    title.toLowerCase.replace(" ", "-").replace("ö", "oe").replace("ü", "ue").replace("ä", "ae").replace("ß", "ss").replace(":", "")
  }

  private def checkFile(file: File): Try[File] = {
    if (file.exists())
      Success(file)
    else
      Failure(new FileNotFoundException(s"File $file not found"))
  }

  private def withDb(block: Database => Unit) = {
    val db = dbConfig.db
    try {
      block(db)
    } finally db.close
  }

}