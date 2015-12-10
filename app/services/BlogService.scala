package services

import javax.inject.{ Inject, Singleton }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import dao._
import viewmodels.BlogEntryData
import models.BlogEntry
import models.Category
import models.Tag

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

  def getByUrlWithMeta(url: String) = blogEntryDAO.getWithMetaByUrl(url)

  def getByUrlWithMetaRequired(url: String) = require(getByUrlWithMeta(url))

  def getListWithMeta() = blogEntryDAO.getListWithMeta() map { BlogEntryData(_) }

  def getTagRequired(url: String) = require(tagDAO.getByUrl(url))

  def getCategoryRequired(url: String) = require(catDAO.getByUrl(url))

  def getAttachment(blogurl: String, url: String) = attachmentDAO.getByBlogUrlAndAttachmentUrl(blogurl, url)

  def getAttachmentRequired(blogurl: String, url: String) = require(getAttachment(blogurl, url))

  def getAllCategories() = catDAO.getAll()

  def getAllTags() = tagDAO.getAll()

  def insertBlogEntry(blog: BlogEntry) = blogEntryDAO.insert(blog)

  def insertCategory(category: Category) = catDAO.insert(category)

  def insertTag(tag: Tag) = tagDAO.insert(tag)

  def updateBlogContent(id: Int, content: String, contentRendered: String, abstractRendered: String) =
    blogEntryDAO.updateContent(id, content, contentRendered, abstractRendered)

}