package dao

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider

import models._
import slick.driver.MySQLDriver.api._

@Singleton
class AttachmentDAO @Inject() (dbConfigProvider: DatabaseConfigProvider)
    extends GenericDAO[Attachment, AttachmentTable](dbConfigProvider, Attachments) {

  def getByBlogIdAndUrl(blogid: Int, url: String) = db.run {
    Attachments.filter(attachment => (attachment.url === url) && (attachment.blogId === blogid)).result.headOption
  }

}