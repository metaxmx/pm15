package models

import org.joda.time.DateTime

import com.github.tototoshi.slick.MySQLJodaSupport.datetimeTypeMapper

import slick.driver.MySQLDriver.api._
import slick.lifted.{ Tag => SlickTag }

case class BlogEntry(
  id: Int,
  categoryId: Int,
  url: String,
  title: String,
  content: String,
  contentRendered: String,
  contentFormat: String,
  published: Boolean,
  publishedDate: Option[DateTime],
  views: Int) extends KeyedEntity

class BlogEntryTable(tag: SlickTag)
    extends Table[BlogEntry](tag, "blog") with KeyedEntityTable {

  def id = column[Int]("id", O.PrimaryKey)
  def categoryId = column[Int]("category_id")
  def url = column[String]("url")
  def title = column[String]("title")
  def content = column[String]("content")
  def contentRendered = column[String]("content_rendered")
  def contentFormat = column[String]("content_format")
  def published = column[Boolean]("published")
  def publishedDate = column[Option[DateTime]]("published_date")
  def views = column[Int]("views")

  def blogCategory = foreignKey("fk_blog_category", categoryId, Categories)(_.id)

  def * = (id, categoryId, url, title, content, contentRendered, contentFormat, published, publishedDate, views) <>
    (BlogEntry.tupled, BlogEntry.unapply)
}

object BlogEntries extends TableQuery(new BlogEntryTable(_))