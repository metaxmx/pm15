package models

import slick.driver.MySQLDriver.api._
import slick.lifted.{ Tag => SlickTag }

case class Tag(
  id: Int,
  url: String,
  title: String) extends KeyedEntity

class TagTable(tag: SlickTag)
    extends Table[Tag](tag, "tag") with KeyedEntityTable {

  def id = column[Int]("id", O.PrimaryKey)
  def url = column[String]("url")
  def title = column[String]("title")

  def * = (id, url, title) <> (Tag.tupled, Tag.unapply)
}

object Tags extends TableQuery(new TagTable(_))
