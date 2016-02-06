package dao

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models._
import slick.driver.MySQLDriver.api._

@Singleton
class CategoryDAO @Inject() (dbConfigProvider: DatabaseConfigProvider)
    extends GenericDAO[Category, CategoryTable](dbConfigProvider, Categories) {

  def getByUrl(url: String): Future[Option[Category]] = db.run {
    Categories.filter(_.url === url).result.headOption
  }

  def update(id: Int, title: String, url: String): Future[Boolean] = db.run {
    val query = for {
      category <- Categories if category.id === id
    } yield (category.title, category.url)
    query.update(title, url)
  } map {
    numChanged => numChanged > 0
  }

  def getBlogCountByCategories() = db.run {
    BlogEntries.groupBy(_.categoryId).map { case (catId, subQuery) => (catId, subQuery.countDistinct) }.result
  }

}