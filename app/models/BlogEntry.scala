package models

import slick.lifted.Tag
import slick.driver.MySQLDriver.api._
import com.github.tototoshi.slick.MySQLJodaSupport._
import org.joda.time.DateTime
import slick.lifted.TableQuery


case class BlogEntry(
  id: Int,
  url: String,
  title: String,
  content: String,
  contentRendered: String,
  published: Boolean,
  publishedDate: DateTime)

class BlogEntryTable(tag: Tag)
    extends Table[BlogEntry](tag, "blog") {

  def id = column[Int]("id", O.PrimaryKey)
  def url = column[String]("url")
  def title = column[String]("title")
  def content = column[String]("content")
  def contentRendered = column[String]("content_rendered")
  def published = column[Boolean]("published")
  def publishedDate = column[DateTime]("published_date")

  def * = (id, url, title, content, contentRendered, published, publishedDate) <> (BlogEntry.tupled, BlogEntry.unapply)
}

object BlogEntries extends TableQuery(new BlogEntryTable(_)) {

}