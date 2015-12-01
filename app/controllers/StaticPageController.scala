package controllers

import javax.inject.Singleton
import javax.inject.Inject
import play.api.mvc._
import util.exception.PageExceptions
import play.api.libs.concurrent.Execution.Implicits._

@Singleton
class StaticPageController extends AbstractController {

  def showIndexPage = PageAction {
    Ok(views.html.page_index())
  }

  def showImpressumPage = PageAction {
    Ok(views.html.page_impressum())
  }

  def showDatenschutzPage = PageAction {
    Ok(views.html.page_datenschutz())
  }

  def showProfilPage = PageAction {
    Ok(views.html.page_profil())
  }

  def showKontaktPage = PageAction {
    Ok(views.html.page_kontakt())
  }

}