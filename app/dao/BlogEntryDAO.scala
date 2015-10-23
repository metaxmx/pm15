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
      case (blog, cat) =>
        val result = BlogEntryWithMeta(blog, cat, tags)
        println(result)
        result
    }
  }

}