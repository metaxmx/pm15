package models

import slick.lifted.Tag
import slick.driver.MySQLDriver.api._

case class StaticPage(
  id: Int,
  url: String,
  title: String,
  content: String)

class StaticPageTable(tag: Tag)
    extends Table[StaticPage](tag, "static") {

  def id = column[Int]("id", O.PrimaryKey)
  def url = column[String]("url")
  def title = column[String]("title")
  def content = column[String]("content")

  def * = (id, url, title, content) <> (StaticPage.tupled, StaticPage.unapply)
}

object StaticPages extends TableQuery(new StaticPageTable(_)) {

}