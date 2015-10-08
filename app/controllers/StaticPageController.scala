package controllers

import javax.inject.Singleton
import javax.inject.Inject
import services.StaticPageService
import play.api.mvc._
import util.exception.PageExceptions
import play.api.libs.concurrent.Execution.Implicits._

@Singleton
class StaticPageController @Inject() (staticPageService: StaticPageService) extends AbstractController {

  def showIndexPage = showStaticPage("index")

  def showStaticPage(url: String) = PageAction.async {
    staticPageService.getStaticPage(url) map {
      _.fold {
        throw PageExceptions.pageNotFoundException
      } {
        staticPage =>
          Ok(views.html.staticpage(staticPage))
      }
    }
  }

}