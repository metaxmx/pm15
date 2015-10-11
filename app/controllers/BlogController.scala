package controllers

import javax.inject.Singleton
import javax.inject.Inject
import services.StaticPageService
import play.api.mvc._
import util.exception.PageExceptions
import play.api.libs.concurrent.Execution.Implicits._
import services.BlogService

@Singleton
class BlogController @Inject() (blogService: BlogService) extends AbstractController {
  
//  def blogOverview = blogOverviewPage(1)
//  
//  def blogOverviewPage(page: Int) = PageAction.async {
//    blogService.getAllBlogEntries().map {
//      entries =>
//        val visibleEntries = entries.filter(_.published)
//    }
//  }
  
}