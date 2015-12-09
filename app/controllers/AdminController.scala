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
   * Pages
   */

  def adminPage = AdminAction {
    Ok(views.html.admin())
  }

  def editBlogEntryPage(id: Int) = AdminAction {
    Ok(views.html.edit_blog(id))
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

  implicit val formatBlogEntryNotFOund = Json.format[BlogEntryNotFound]

  implicit val formatBlogEntryDataCategory = Json.format[BlogEntryDataCategory]

  implicit val formatBlogEntryDataTag = Json.format[BlogEntryDataTag]

  implicit val formatBlogEntryData = Json.format[BlogEntryData]

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

  def getRestBlogEntry(id: Int) = AdminAction.async {
    blogService.getByIdWithMeta(id) map {
      case None => Ok(Json.toJson(BlogEntryNotFound(false))).as(JSON)
      case Some(blog) => {
        val blogData = BlogEntryData(true, blog.blogEntry.title, blog.blogEntry.url, blog.blogEntry.content,
          blog.blogEntry.abstractRendered, blog.blogEntry.contentRendered, blog.blogEntry.contentFormat, blog.blogEntry.published,
          blog.blogEntry.publishedDate map (fullDateTimeFormat print _),
          BlogEntryDataCategory(blog.category.id, blog.category.title, blog.category.url),
          blog.tags.map { tag => BlogEntryDataTag(tag.id, tag.title, tag.url) }, blog.blogEntry.views)
        Ok(Json.toJson(blogData)).as(JSON)
      }
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

  case class BlogEntryNotFound(success: Boolean)

  case class BlogEntryDataCategory(id: Int, title: String, url: String)

  case class BlogEntryDataTag(id: Int, title: String, url: String)

  case class BlogEntryData(success: Boolean, title: String, url: String, content: String, abstractRendered: String, contentRendered: String,
                           contentFormat: String, published: Boolean, publishedDate: Option[String], category: BlogEntryDataCategory,
                           tags: Seq[BlogEntryDataTag], views: Int)

}