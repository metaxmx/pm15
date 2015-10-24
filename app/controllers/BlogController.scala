package controllers

import javax.inject.Singleton
import javax.inject.Inject
import services.StaticPageService
import play.api.mvc._
import util.exception.PageExceptions
import play.api.libs.concurrent.Execution.Implicits._
import services.BlogService
import viewmodels.BlogEntryWithMeta
import viewmodels.BlogEntryList
import viewmodels.Pagination
import viewmodels.BlogEntryWithMeta

@Singleton
class BlogController @Inject() (blogService: BlogService) extends AbstractController {

  val BLOG_ENTRIES_PER_PAGE = 10

  def blogOverview = blogOverviewPage(1)

  def blogOverviewPage(page: Int) = {
    val list = blogService.getListWithMeta()
    list.onSuccess {
      case data: Seq[BlogEntryWithMeta] =>
        println("------------------")
        data.foreach { println(_) }
        println("------------------")
    }
    ????
  }

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

  private def filterBlogEntries(filteredBlogEntries: Seq[BlogEntryWithMeta], page: Int): Option[BlogEntryList] =
    if (filteredBlogEntries.isEmpty)
      None
    else {
      val entriesGrouped = filteredBlogEntries.grouped(BLOG_ENTRIES_PER_PAGE).toSeq
      val pageIndex = page - 1
      if (pageIndex < 0 || pageIndex >= entriesGrouped.size || entriesGrouped(pageIndex).isEmpty)
        None
      else {
        Some(BlogEntryList(entriesGrouped(pageIndex), getPagination(page, entriesGrouped.size)))
      }
    }

  private def getPagination(current: Int, max: Int) = {
    val (first, prev) = if (current <= 1) (Some(1), Some(current - 1)) else (None, None)
    val (next, last) = if (current >= max) (Some(current + 1), Some(max)) else (None, None)
    Pagination(first, prev, current, next, last)
  }

}