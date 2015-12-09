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
import models.BlogEntry
import util.renderers.MarkdownContentRenderer
import models.Category
import models.Tag

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

  import AdminController._

  implicit val formatBlogListEntry = Json.format[BlogListEntry]

  implicit val formatBlogList = Json.format[BlogList]

  implicit val formatCategoryListEntry = Json.format[CategoryListEntry]

  implicit val formatCategoryList = Json.format[CategoryList]

  implicit val formatTagListEntry = Json.format[TagListEntry]

  implicit val formatTagList = Json.format[TagList]

  implicit val formatInsertBlogEntryData = Json.format[InsertBlogEntryData]

  implicit val formatInsertCategoryData = Json.format[InsertCategoryData]

  implicit val formatInsertTagData = Json.format[InsertTagData]

  implicit val formatInserSuccess = Json.format[InsertSuccess]

  implicit val formatInsertError = Json.format[InsertError]

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

  def getRestCategoryList = AdminAction.async {
    blogService.getAllCategories() map {
      categories =>
        // TODO: Fetch number of blog entries
        val categoryList = CategoryList(categories map (cat => CategoryListEntry(cat.id, cat.title, cat.url, 0)))
        Ok(Json.toJson(categoryList)).as(JSON)
    }
  }

  def getRestTagList = AdminAction.async {
    blogService.getAllTags() map {
      tags =>
        // TODO: Fetch number of blog entries
        val tagList = TagList(tags map (tag => TagListEntry(tag.id, tag.title, tag.url, 0)))
        Ok(Json.toJson(tagList)).as(JSON)
    }
  }

  def postRestAddEntry = AdminAction.async(parse.json[InsertBlogEntryData]) {
    request =>
      val insertEntryData = request.body
      val blogEntry = BlogEntry(0, insertEntryData.category, insertEntryData.url, insertEntryData.title, "", "", "",
        MarkdownContentRenderer.renderFormat, false, None, 0)
      blogService.insertBlogEntry(blogEntry) map {
        id =>
          Ok(Json.toJson(InsertSuccess(true, id))).as(JSON)
      } recover {
        case exc: Exception =>
          Ok(Json.toJson(InsertError(false, exc.getMessage))).as(JSON)
      }
  }

  def postRestAddCategory = AdminAction.async(parse.json[InsertCategoryData]) {
    request =>
      val insertCategoryData = request.body
      val category = Category(0, insertCategoryData.url, insertCategoryData.title)
      blogService.insertCategory(category) map {
        id =>
          Ok(Json.toJson(InsertSuccess(true, id))).as(JSON)
      } recover {
        case exc: Exception =>
          Ok(Json.toJson(InsertError(false, exc.getMessage))).as(JSON)
      }
  }

  def postRestAddTag = AdminAction.async(parse.json[InsertTagData]) {
    request =>
      val insertTagData = request.body
      val tag = Tag(0, insertTagData.url, insertTagData.title)
      blogService.insertTag(tag) map {
        id =>
          Ok(Json.toJson(InsertSuccess(true, id))).as(JSON)
      } recover {
        case exc: Exception =>
          Ok(Json.toJson(InsertError(false, exc.getMessage))).as(JSON)
      }
  }

}

object AdminController {

  val fullDateTimeFormat = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")

  case class BlogList(entries: Seq[BlogListEntry])

  case class BlogListEntry(id: Int, title: String, url: String, published: Boolean, publishedDate: Option[String],
                           category: String, tags: Seq[String], views: Int)

  case class CategoryList(entries: Seq[CategoryListEntry])

  case class CategoryListEntry(id: Int, title: String, url: String, blogEntries: Int)

  case class TagList(entries: Seq[TagListEntry])

  case class TagListEntry(id: Int, title: String, url: String, blogEntries: Int)

  case class InsertBlogEntryData(title: String, url: String, category: Int)

  case class InsertCategoryData(title: String, url: String)

  case class InsertTagData(title: String, url: String)

  case class InsertSuccess(success: Boolean, id: Int)

  case class InsertError(success: Boolean, error: String)

}