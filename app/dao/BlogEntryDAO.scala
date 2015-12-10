package dao

import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import javax.inject._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver
import models._
import scala.concurrent.Future
import viewmodels.BlogEntryWithMeta
import play.api.libs.concurrent.Execution.Implicits._
import viewmodels.BlogEntryWithMeta

@Singleton
class BlogEntryDAO @Inject() (dbConfigProvider: DatabaseConfigProvider)
    extends GenericDAO[BlogEntry, BlogEntryTable](dbConfigProvider, BlogEntries) {

  def getByUrl(url: String): Future[Option[BlogEntry]] = db.run {
    BlogEntries.filter(_.url === url).result.headOption
  }

  def getWithMetaById(id: Int): Future[Option[BlogEntryWithMeta]] = db.run {
    val queryBlog = for {
      (blog, cat) <- BlogEntries join Categories on (_.categoryId === _.id) if (blog.id === id)
    } yield (blog, cat)
    val resultBlog = queryBlog.result.headOption
    val queryTags = for {
      (blogHasTag, blogTag) <- BlogEntryHasTags join Tags on (_.tagId === _.id) if (blogHasTag.blogId === id)
    } yield blogTag
    val resultTags = queryTags.sortBy { _.title }.result
    for {
      maybeBlogCat <- resultBlog
      tags <- resultTags
    } yield maybeBlogCat map {
      case (blog, cat) => BlogEntryWithMeta(blog, cat, tags)
    }
  }

  def getWithMetaByUrl(url: String): Future[Option[BlogEntryWithMeta]] = db.run {
    val queryBlog = for {
      (blog, cat) <- BlogEntries join Categories on (_.categoryId === _.id) if (blog.url === url)
    } yield (blog, cat)
    val resultBlog = queryBlog.result.headOption
    val queryTags = for {
      ((blogHasTag, blogTag), blog) <- BlogEntryHasTags join Tags on (_.tagId === _.id) join BlogEntries on (_._1.blogId === _.id) if (blog.url === url)
    } yield blogTag
    val resultTags = queryTags.sortBy { _.title }.result
    for {
      maybeBlogCat <- resultBlog
      tags <- resultTags
    } yield maybeBlogCat map {
      case (blog, cat) => BlogEntryWithMeta(blog, cat, tags)
    }
  }

  def getListWithMeta(): Future[Seq[BlogEntryWithMeta]] = db.run {
    val queryBlog = for {
      ((blog, cat), blogTagOpt) <- (BlogEntries join Categories on (_.categoryId === _.id)) joinLeft
        (BlogEntryHasTags join Tags on ((a, b) => a.tagId === b.id)) on (_._1.id === _._1.blogId)
    } yield (blog, cat, blogTagOpt)
    val resultBlog = queryBlog.result
    resultBlog map {
      _.map {
        case (blog, cat, None)                    => (blog, cat, None, None)
        case (blog, cat, Some((blogHasTag, tag))) => (blog, cat, Some(blogHasTag), Some(tag))
      }.groupBy(_._1.id).values.map {
        seq =>
          val blog = seq.head._1
          val cat = seq.head._2
          val tags = seq filter (_._4.isDefined) map (_._4.get)
          BlogEntryWithMeta(blog, cat, tags)
      }.toSeq.sorted
    }
  }

  def updateContent(id: Int, content: String, contentRendered: String, abstractRendered: String): Future[Boolean] = db.run {
    val query = for {
      blogEntry <- BlogEntries if blogEntry.id === id
    } yield (blogEntry.content, blogEntry.contentRendered, blogEntry.abstractRendered)
    val updateAction = query.update(content, contentRendered, abstractRendered)
    updateAction
  } map {
    numChanged => numChanged > 0
  }

}