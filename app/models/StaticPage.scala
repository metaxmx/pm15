package models

import slick.driver.MySQLDriver.api._
import slick.lifted.{ Tag => SlickTag }

case class StaticPage(
  id: Int,
  url: String,
  title: String,
  content: String,
  contentRendered: String,
  contentFormat: String) extends KeyedEntity

class StaticPageTable(tag: SlickTag)
    extends Table[StaticPage](tag, "static") with KeyedEntityTable {

  def id = column[Int]("id", O.PrimaryKey)
  def url = column[String]("url")
  def title = column[String]("title")
  def content = column[String]("content")
  def contentRendered = column[String]("content_rendered")
  def contentFormat = column[String]("content_format")

  def * = (id, url, title, content, contentRendered, contentFormat) <> (StaticPage.tupled, StaticPage.unapply)
}

object StaticPages extends TableQuery(new StaticPageTable(_))