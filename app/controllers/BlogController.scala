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

  def blogOverview = blogOverviewPage(1)

  def blogOverviewPage(page: Int) = ????

  def blogByCategory(url: String) = blogByCategoryPage(url, 1)

  def blogByCategoryPage(url: String, page: Int) = ????

  def blogByTag(url: String) = blogByTagPage(url, 1)

  def blogByTagPage(url: String, page: Int) = ????

  def blogByYear(year: Int) = blogByYearPage(year, 1)

  def blogByYearPage(year: Int, page: Int) = ????
  
  def blogByMonth(year: Int, month: Int) = blogByMonthPage(year, month, 1)

  def blogByMonthPage(year: Int, month: Int, page: Int) = ????

  def showBlogEntry(id: Int, url: String) = PageAction.async {
    println("Blog ID " + id)
    for {
      blogEntry <- blogService.getByIdWithMetaRequired(id)
    } yield Ok(views.html.blogentry(blogEntry))
  }

}