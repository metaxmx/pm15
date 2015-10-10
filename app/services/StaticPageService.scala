package services

import models.StaticPage
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import dao.StaticPageDAO
import scala.concurrent.Future

@Singleton
class StaticPageService @Inject()(staticPageDAO: StaticPageDAO) {

  def getStaticPage(url: String): Future[Option[StaticPage]] = staticPageDAO.getByUrl(url)

  def getStaticPageById(id: Int): Future[Option[StaticPage]] = staticPageDAO.getById(id)

  def getAllStaticPages(): Future[Seq[StaticPage]] = staticPageDAO.getAll()

}