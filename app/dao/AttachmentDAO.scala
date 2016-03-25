package dao

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models._
import models.AttachmentTypes.AttachmentType
import slick.driver.MySQLDriver.api._

@Singleton
class AttachmentDAO @Inject() (dbConfigProvider: DatabaseConfigProvider)
    extends GenericDAO[Attachment, AttachmentTable](dbConfigProvider, Attachments) {

  def getByBlogIdAndUrl(blogid: Int, url: String) = db.run {
    Attachments.filter(attachment => (attachment.url === url) && (attachment.blogId === blogid)).result.headOption
  }

  def getByBlogUrlAndAttachmentUrl(blogurl: String, url: String) = db.run {
    val queryAttachment = for {
      (blog, attachment) <- BlogEntries join Attachments on (_.id === _.blogId)
      if ((blog.url === blogurl) && (attachment.url === url))
    } yield (attachment)
    queryAttachment.result.headOption
  }

  def getByBlogUrl(blogurl: String) = db.run {
    val queryAttachment = for {
      (blog, attachment) <- BlogEntries join Attachments on (_.id === _.blogId) if (blog.url === blogurl)
    } yield (attachment)
    queryAttachment.result
  }

  def getByBlogId(blogId: Int) = db.run {
    Attachments.filter(_.blogId === blogId).result
  }

  def update(id: Int, url: String, mime: String, attachmentType: AttachmentType): Future[Boolean] = this.getById(id) flatMap {
    case Some(oldAttachment) => {
      db.run {
        // If filename was previously not set, copy from previous URL as the url might have been changed,
        // while the filename remained unchanged
        val filename = oldAttachment.filename.getOrElse(oldAttachment.url)
        val query = for {
          attachment <- Attachments if attachment.id === id
        } yield (attachment.url, attachment.mime, attachment.attachmentType, attachment.filename)
        query.update(url, mime, attachmentType, Some(filename))
      } map {
        numChanged => numChanged > 0
      }
    }
    case None => Future.successful(false)
  }
    

}