package controllers

import javax.inject.Singleton
import javax.inject.Inject
import services.StaticPageService
import play.api.mvc._
import util.exception.PageExceptions

@Singleton
class StaticPageController @Inject() (staticPageService: StaticPageService) extends AbstractController {

  def showStaticPage(url: String) = PageAction {
    staticPageService.getStaticPage(url).fold {
      throw PageExceptions.pageNotFoundException
    } {
      staticPage =>
        Ok(views.html.staticpage(staticPage))
    }
  }

}