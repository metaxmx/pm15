package services

import models.BlogEntry
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import dao.StaticPageDAO
import scala.concurrent.Future
import dao.BlogEntryDAO

@Singleton
class BlogService @Inject()(blogEntryDAO: BlogEntryDAO) {

}