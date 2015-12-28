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
    abstractRendered: String,
    contentFormat: String,
    published: Boolean,
    publishedDate: Option[DateTime],
    views: Int) extends KeyedEntity with Ordered[BlogEntry] {

  override def compare(other: BlogEntry): Int = (publishedDate, other.publishedDate) match {
    // If none published, compare by id (desc)
    case (None, None)                => -(id compareTo other.id)
    // If other published, this is first
    case (None, Some(_))             => -1
    // If this published, other is first
    case (Some(_), None)             => 1
    // If both published, compare by date (desc)
    case (Some(pub), Some(otherPub)) => -(pub compareTo otherPub)
  }

}

class BlogEntryTable(tag: SlickTag)
    extends Table[BlogEntry](tag, "blog") with KeyedEntityTable {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def categoryId = column[Int]("category_id")
  def url = column[String]("url")
  def title = column[String]("title")
  def content = column[String]("content")
  def contentRendered = column[String]("content_rendered")
  def abstractRendered = column[String]("abstract_rendered")
  def contentFormat = column[String]("content_format")
  def published = column[Boolean]("published")
  def publishedDate = column[Option[DateTime]]("published_date")
  def views = column[Int]("views")

  def blogCategory = foreignKey("fk_blog_category", categoryId, Categories)(_.id)

  def * = (id, categoryId, url, title, content, contentRendered, abstractRendered, contentFormat, published, publishedDate, views) <>
    (BlogEntry.tupled, BlogEntry.unapply)
}

object BlogEntries extends TableQuery(new BlogEntryTable(_)) with BaseTableQuery
