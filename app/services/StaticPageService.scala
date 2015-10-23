package services

import models.StaticPage
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import dao.StaticPageDAO
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import models.StaticPage

@Singleton
class StaticPageService @Inject() (staticPageDAO: StaticPageDAO) extends GenericService {

  def getById(id: Int): Future[Option[StaticPage]] = staticPageDAO.getById(id)

  def getByUrl(url: String): Future[Option[StaticPage]] = staticPageDAO.getByUrl(url)

  def getAll(): Future[Seq[StaticPage]] = staticPageDAO.getAll()

  def getByIdRequired(id: Int): Future[StaticPage] = require(getById(id))

  def getByUrlRequired(url: String): Future[StaticPage] = require(getByUrl(url))

}