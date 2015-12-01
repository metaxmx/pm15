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

case class StaticPageData(title: String, url: String, content: String)

@Singleton
class AdminController @Inject() (blogService: BlogService, val messagesApi: MessagesApi) extends AbstractController with I18nSupport {

  /*
   * Overview
   */

  def adminPage = AdminAction.async {
    for {
      blogEntries <- blogService.getAll()
    } yield Ok(views.html.admin(blogEntries))
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