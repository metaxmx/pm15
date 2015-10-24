package dao

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider

import models._
import slick.driver.MySQLDriver.api._

@Singleton
class CategoryDAO @Inject() (dbConfigProvider: DatabaseConfigProvider)
    extends GenericDAO[Category, CategoryTable](dbConfigProvider, Categories) {

  def getByUrl(url: String): Future[Option[Category]] = db.run {
    Categories.filter(_.url === url).result.headOption
  }

}