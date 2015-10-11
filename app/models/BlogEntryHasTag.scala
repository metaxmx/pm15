package models

import slick.driver.MySQLDriver.api._
import slick.lifted.{ Tag => SlickTag }

case class BlogEntryHasTag(
  id: Int,
  blogId: Int,
  tagId: Int) extends KeyedEntity

class BlogEntryHasTagTable(tag: SlickTag)
    extends Table[BlogEntryHasTag](tag, "blog_has_tag") with KeyedEntityTable {

  def id = column[Int]("id", O.PrimaryKey)
  def blogId = column[Int]("blog_id")
  def tagId = column[Int]("tag_id")

  def blogEntry = foreignKey("fk_bloghastag_blog", blogId, BlogEntries)(_.id)
  def tag = foreignKey("fk_bloghastag_tag", tagId, Tags)(_.id)

  def * = (id, blogId, tagId) <> (BlogEntryHasTag.tupled, BlogEntryHasTag.unapply)
}

object BlogEntryHasTags extends TableQuery(new BlogEntryHasTagTable(_))