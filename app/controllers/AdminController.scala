package controllers

import javax.inject._
import services.StaticPageService
import services.BlogService
import play.api.libs.concurrent.Execution.Implicits._
import util.exception.PageExceptions
import scala.concurrent.Future
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

case class StaticPageData(title: String, url: String, content: String)

@Singleton
class AdminController @Inject() (staticPageService: StaticPageService, blogService: BlogService, val messagesApi: MessagesApi) extends AbstractController with I18nSupport {

  /*
   * Overview
   */

  def adminPage = AdminAction.async {
    for {
      staticPages <- staticPageService.getAllStaticPages()
      blogEntries <- blogService.getAllBlogEntries()
    } yield Ok(views.html.admin(staticPages, blogEntries))
  }

  /*
   * Static Pages
   */

  val staticPageDataForm = Form(
    mapping(
      "title" -> nonEmptyText,
      "url" -> nonEmptyText,
      "content" -> nonEmptyText)(StaticPageData.apply)(StaticPageData.unapply))

  def showEditStaticPage(id: Int) = AdminAction.async {
    implicit request =>
      staticPageService.getStaticPageById(id) map {
        _.fold {
          throw PageExceptions.pageNotFoundException
        } {
          staticPage =>
            val formData = StaticPageData(staticPage.title, staticPage.url, staticPage.content)
            val form = staticPageDataForm.fill(formData)
            Ok(views.html.adminstatic(staticPage, form))
        }
      }
  }

  def saveEditStaticPage(id: Int) = AdminAction.async {
    implicit request =>
      val form = staticPageDataForm.bindFromRequest
      staticPageService.getStaticPageById(id) map {
        _.fold {
          throw PageExceptions.pageNotFoundException
        } {
          staticPage =>
            Ok(views.html.adminstatic(staticPage, form))
        }
      }
  }

  def showNewStaticPage() = AdminAction.async {
    Future successful Ok(views.html.error_notfound("dummy"))
  }

  def saveNewStaticPage() = AdminAction.async {
    Future successful Ok(views.html.error_notfound("dummy"))
  }

  def showEditBlogEntry(id: Int) = AdminAction.async {
    Future successful Ok(views.html.error_notfound("dummy"))
  }

  def saveEditBlogEntry(id: Int) = AdminAction.async {
    Future successful Ok(views.html.error_notfound("dummy"))
  }

  def showNewBlogEntry() = AdminAction.async {
    Future successful Ok(views.html.error_notfound("dummy"))
  }

  def saveNewBlogEntry() = AdminAction.async {
    Future successful Ok(views.html.error_notfound("dummy"))
  }

}