package dao

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider

import models._
import models.{ Tag => BlogTag }
import slick.driver.MySQLDriver.api._

@Singleton
class TagDAO @Inject() (dbConfigProvider: DatabaseConfigProvider)
    extends GenericDAO[BlogTag, TagTable](dbConfigProvider, Tags) {

  def getByUrl(url: String): Future[Option[BlogTag]] = db.run {
    Tags.filter(_.url === url).result.headOption
  }

}