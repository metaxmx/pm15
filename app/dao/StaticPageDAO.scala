package dao

import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import javax.inject._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver
import models.StaticPage
import models.StaticPages
import scala.concurrent.Future

@Singleton
class StaticPageDAO @Inject()(dbConfigProvider: DatabaseConfigProvider) {
  
  implicit val db = dbConfigProvider.get[MySQLDriver].db
  
  def getStaticPage(url: String): Future[Option[StaticPage]] = db.run {
    StaticPages.filter(_.url === url).result.headOption
  }
  
}