package controllers

import javax.inject.Singleton
import javax.inject.Inject
import services.StaticPageService
import play.api.mvc._
import util.exception.PageExceptions
import play.api.libs.concurrent.Execution.Implicits._

@Singleton
class StaticPageController @Inject() (staticPageService: StaticPageService) extends AbstractController {

  val indexUrl = "index"

  def showIndexPage = showStaticPage(indexUrl)

  def showStaticPage(url: String) = PageAction.async {
    staticPageService.getByUrlRequired(url) map {
      staticPage =>
        Ok(views.html.staticpage(staticPage))
    }
  }

}