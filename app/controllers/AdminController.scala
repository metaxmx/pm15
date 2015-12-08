package controllers

import javax.inject._
import services.BlogService
import play.api.libs.concurrent.Execution.Implicits._
import util.exception.PageExceptions
import scala.concurrent.Future
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import org.joda.time.format.DateTimeFormat
import play.api.http.ContentTypes.JSON
import models.BlogEntry

@Singleton
class AdminController @Inject() (blogService: BlogService, val messagesApi: MessagesApi) extends AbstractController with I18nSupport {

  /*
   * Overview
   */

  def adminPage = AdminAction {
    Ok(views.html.admin())
  }

  /*
   * REST Operations
   */

  val fullDateTimeFormat = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")

  case class BlogList(entries: Seq[BlogListEntry])

  case class BlogListEntry(id: Int, title: String, url: String, published: Boolean, publishedDate: Option[String],
                           category: String, tags: Seq[String], views: Int)

  implicit val formatBlogListEntry = Json.format[BlogListEntry]

  implicit val formatBlogList = Json.format[BlogList]

  def getRestBlogList = AdminAction.async {
    blogService.getListWithMeta() map {
      blogEntryData =>
        val blogEntries = blogEntryData.blogEntries map {
          blogEntryWithMeta =>
            val blog = blogEntryWithMeta.blogEntry
            BlogListEntry(blog.id, blog.title, blog.url, blog.published, blog.publishedDate map (fullDateTimeFormat print _),
              blogEntryWithMeta.category.title, blogEntryWithMeta.tags.map { _.title }, blog.views)
        }
        val blogList = BlogList(blogEntries)
        Ok(Json.toJson(blogList)).as(JSON)
    }
  }

}