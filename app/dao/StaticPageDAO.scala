package dao

import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import javax.inject._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver
import models._
import scala.concurrent.Future

@Singleton
class StaticPageDAO @Inject() (dbConfigProvider: DatabaseConfigProvider)
    extends GenericDAO[StaticPage, StaticPageTable](dbConfigProvider, StaticPages) {

  def getByUrl(url: String): Future[Option[StaticPage]] = db.run {
    StaticPages.filter(_.url === url).result.headOption
  }

}