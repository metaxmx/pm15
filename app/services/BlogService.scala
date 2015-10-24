package services

import models.BlogEntry
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import dao.StaticPageDAO
import scala.concurrent.Future
import dao.BlogEntryDAO
import play.api.libs.concurrent.Execution.Implicits._

@Singleton
class BlogService @Inject() (blogEntryDAO: BlogEntryDAO) extends GenericService {

  def getById(id: Int) = blogEntryDAO.getById(id)

  def getAll() = blogEntryDAO.getAll()

  def getByIdWithMeta(id: Int) = blogEntryDAO.getWithMetaById(id)

  def getByIdRequired(id: Int) = require(getById(id))

  def getByIdWithMetaRequired(id: Int) = require(getByIdWithMeta(id))

  def getListWithMeta() = blogEntryDAO.getListWithMeta()

}