package dao

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models._
import models.{ Tag => BlogTag }
import slick.driver.MySQLDriver.api._

@Singleton
class TagDAO @Inject() (dbConfigProvider: DatabaseConfigProvider)
    extends GenericDAO[BlogTag, TagTable](dbConfigProvider, Tags) {

  def getByUrl(url: String): Future[Option[BlogTag]] = db.run {
    Tags.filter(_.url === url).result.headOption
  }

  def update(id: Int, title: String, url: String): Future[Boolean] = db.run {
    val query = for {
      tag <- Tags if tag.id === id
    } yield (tag.title, tag.url)
    query.update(title, url)
  } map {
    numChanged => numChanged > 0
  }

  def getBlogCountByTags() = db.run {
    BlogEntryHasTags.groupBy(_.tagId).map { case (tagId, subQuery) => (tagId, subQuery.countDistinct) }.result
  }

  def assignTag(blogEntryId: Int, tagId: Int): Future[Boolean] = db.run {
    val q = (BlogEntryHasTags) += BlogEntryHasTag(0, blogEntryId, tagId)
    println("INS Query: " + q.statements)
    q
  } map {
    numChanged => numChanged > 0
  }

  def unassignTag(blogEntryId: Int, tagId: Int): Future[Boolean] = db.run {
    val query = for {
      blogEntryHasTag <- BlogEntryHasTags if blogEntryHasTag.blogId === blogEntryId && blogEntryHasTag.tagId === tagId
    } yield blogEntryHasTag
    println("DEL Query: " + query.delete.statements)
    query.delete
  } map {
    numChanged => numChanged > 0
  }

}