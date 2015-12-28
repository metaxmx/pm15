package models

import slick.driver.MySQLDriver.api._
import slick.lifted.{ Tag => SlickTag }

case class Category(
  id: Int,
  url: String,
  title: String) extends KeyedEntity

class CategoryTable(tag: SlickTag)
    extends Table[Category](tag, "category") with KeyedEntityTable {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def url = column[String]("url")
  def title = column[String]("title")

  def * = (id, url, title) <> (Category.tupled, Category.unapply)
}

object Categories extends TableQuery(new CategoryTable(_)) with BaseTableQuery

