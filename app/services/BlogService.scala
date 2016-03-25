package services

import javax.inject.{ Inject, Singleton }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import dao._
import models._
import viewmodels.BlogEntryData
import org.joda.time.DateTime
import scala.concurrent.Future
import models.AttachmentTypes.AttachmentType

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

  def getAttachmentById(id: Int) = attachmentDAO.getById(id)

  def getAttachment(blogurl: String, url: String) = attachmentDAO.getByBlogUrlAndAttachmentUrl(blogurl, url)

  def getAttachmentRequired(blogurl: String, url: String) = require(getAttachment(blogurl, url))

  def getAttachments(blogurl: String) = attachmentDAO.getByBlogUrl(blogurl)

  def getAttachmentsByBlogId(blogId: Int) = attachmentDAO.getByBlogId(blogId)

  def getAllCategories() = catDAO.getAll()

  def getAllCategoriesWithBlogCount() = for {
    categories <- catDAO.getAll()
    blogCountByCat <- catDAO.getBlogCountByCategories()
  } yield categories map { cat => (cat, blogCountByCat.toMap.get(cat.id).getOrElse(0)) }

  def getAllTags() = tagDAO.getAll()

  def getAllTagsWithBlogCount() = for {
    tags <- tagDAO.getAll()
    blogCountByTag <- tagDAO.getBlogCountByTags()
  } yield tags map { tag => (tag, blogCountByTag.toMap.get(tag.id).getOrElse(0)) }

  def insertBlogEntry(blog: BlogEntry) = blogEntryDAO.insert(blog)

  def insertCategory(category: Category) = catDAO.insert(category)

  def insertTag(tag: Tag) = tagDAO.insert(tag)

  def insertAttachment(attachment: Attachment) = attachmentDAO.insert(attachment)

  def updateBlogContent(id: Int, content: String, contentRendered: String, abstractRendered: String) =
    blogEntryDAO.updateContent(id, content, contentRendered, abstractRendered)

  def updateBlogMeta(id: Int, title: String, url: String, categoryId: Int, published: Boolean, publishedDate: Option[DateTime]) =
    blogEntryDAO.updateMeta(id, title, url, categoryId, published, publishedDate)

  def updateTags(blogEntryId: Int, currentTags: Set[Int], updatedTags: Set[Int]): Future[Boolean] = {
	  val toAdd = updatedTags -- currentTags
    val toDelete = currentTags -- updatedTags
    val addActions = (Future.successful(true) /: toAdd) {
	    (future, tagId) => future flatMap { prev => tagDAO.assignTag(blogEntryId, tagId).map(_ & prev) }
	  }
	  val addAndDelActions = (addActions /: toDelete) {
	    (future, tagId) => future flatMap { prev => tagDAO.unassignTag(blogEntryId, tagId).map(_ & prev) }
	  }
    addAndDelActions
  }

  def updateCategory(id: Int, title: String, url: String) = catDAO.update(id, title, url)

  def deleteCategory(id: Int) = catDAO.delete(id)

  def updateTag(id: Int, title: String, url: String) = tagDAO.update(id, title, url)

  def deleteTag(id: Int) = tagDAO.delete(id)

  def updateAttachment(id: Int, url: String, mime: String, attachmentType: AttachmentType) =
    attachmentDAO.update(id, url, mime, attachmentType)

  def deleteAttachment(id: Int) = attachmentDAO.delete(id)

}