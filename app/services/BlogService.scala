package services

import javax.inject.{ Inject, Singleton }

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import dao._
import viewmodels.BlogEntryData

@Singleton
class BlogService @Inject() (blogEntryDAO: BlogEntryDAO,
                             tagDAO: TagDAO,
                             catDAO: CategoryDAO,
                             attachmentDAO: AttachmentDAO) extends GenericService {

  def getById(id: Int) = blogEntryDAO.getById(id)

  def getAll() = blogEntryDAO.getAll()

  def getByIdWithMeta(id: Int) = blogEntryDAO.getWithMetaById(id)

  def getByIdRequired(id: Int) = require(getById(id))

  def getByIdWithMetaRequired(id: Int) = require(getByIdWithMeta(id))

  def getListWithMeta() = blogEntryDAO.getListWithMeta() map { BlogEntryData(_) }

  def getTagRequired(url: String) = require(tagDAO.getByUrl(url))

  def getCategoryRequired(url: String) = require(catDAO.getByUrl(url))

  def getAttachment(blogId: Int, url: String) = attachmentDAO.getByBlogIdAndUrl(blogId, url)

  def getAttachmentRequired(blogId: Int, url: String) = require(getAttachment(blogId, url))

}