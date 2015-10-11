package models

import slick.driver.MySQLDriver.api._
import slick.lifted.{ Tag => SlickTag }

case class StaticPage(
  id: Int,
  url: String,
  title: String,
  content: String) extends KeyedEntity

class StaticPageTable(tag: SlickTag)
    extends Table[StaticPage](tag, "static") with KeyedEntityTable {

  def id = column[Int]("id", O.PrimaryKey)
  def url = column[String]("url")
  def title = column[String]("title")
  def content = column[String]("content")

  def * = (id, url, title, content) <> (StaticPage.tupled, StaticPage.unapply)
}

object StaticPages extends TableQuery(new StaticPageTable(_))