package controllers

import javax.inject.{ Inject, Singleton }

import org.joda.time.YearMonth

import play.api.http.Writeable
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Call

import services.BlogService
import util.exception.PageExceptions
import viewmodels._

@Singleton
class BlogController @Inject() (blogService: BlogService) extends AbstractController {

  val BLOG_ENTRIES_PER_PAGE = 1

  def blogOverview = blogOverviewPage(1)

  def blogOverviewPage(page: Int) = PageAction.async {
    showBlogEntryList(page, views.html.blog(_), (_.published),
      routes.BlogController.blogOverview, routes.BlogController.blogOverviewPage)
  }

  def blogByCategory(url: String) = blogByCategoryPage(url, 1)

  def blogByCategoryPage(url: String, page: Int) = PageAction.async {
    for {
      category <- blogService.getCategoryRequired(url)
      result <- showBlogEntryList(page, views.html.blog_by_category(category), (_.published.withCategory(url)),
        routes.BlogController.blogByCategory(category.url), routes.BlogController.blogByCategoryPage(category.url, _))
    } yield result
  }

  def blogByTag(url: String) = blogByTagPage(url, 1)

  def blogByTagPage(url: String, page: Int) = PageAction.async {
    for {
      tag <- blogService.getTagRequired(url)
      result <- showBlogEntryList(page, views.html.blog_by_tag(tag), (_.published.withTag(url)),
        routes.BlogController.blogByTag(tag.url), routes.BlogController.blogByTagPage(tag.url, _))
    } yield result
  }

  def blogByYear(year: Int) = blogByYearPage(year, 1)

  def blogByYearPage(year: Int, page: Int) = PageAction.async {
    val yearMonth = new YearMonth(year, 1)
    showBlogEntryList(page, views.html.blog_by_year(yearMonth), (_.published.withYear(year)),
      routes.BlogController.blogByYear(year), routes.BlogController.blogByYearPage(year, _))
  }

  def blogByMonth(year: Int, month: Int) = blogByMonthPage(year, month, 1)

  def blogByMonthPage(year: Int, month: Int, page: Int) = PageAction.async {
    val yearMonth = new YearMonth(year, if (Range(1, 13) contains month) month else throw PageExceptions.pageNotFoundException)
    showBlogEntryList(page, views.html.blog_by_month(yearMonth), (_.published.withMonth(year, month)),
      routes.BlogController.blogByMonth(year, month), routes.BlogController.blogByMonthPage(year, month, _))
  }

  def showBlogEntry(id: Int, url: String) = PageAction.async {
    println("Blog ID " + id)
    for {
      blogEntry <- blogService.getByIdWithMetaRequired(id)
    } yield Ok(views.html.blogentry(blogEntry))
  }

  private def showBlogEntryList[C](page: Int,
                                   view: BlogEntryList => C,
                                   filter: BlogEntryData => BlogEntryData,
                                   firstPager: Call,
                                   pager: Int => Call)(implicit writeable: Writeable[C]) = {
    blogService.getListWithMeta() map {
      blogData => filterBlogEntries(blogData, page, filter, compoundPager(firstPager, pager))
    } map {
      case None            => throw PageExceptions.pageNotFoundException
      case Some(entryList) => Ok(view(entryList))
    }
  }

  private def compoundPager(firstPager: Call, pager: Int => Call): Int => Call = {
    page =>
      if (page <= 1) firstPager else pager(page)
  }

  private def filterBlogEntries(blogEntryData: BlogEntryData,
                                page: Int,
                                filter: BlogEntryData => BlogEntryData,
                                pager: Int => Call): Option[BlogEntryList] = {
    val filteredEntried = filter(blogEntryData).blogEntries
    if (filteredEntried.isEmpty)
      None
    else {
      val entriesGrouped = filteredEntried.grouped(BLOG_ENTRIES_PER_PAGE).toSeq
      val pageIndex = page - 1
      if (pageIndex < 0 || pageIndex >= entriesGrouped.size || entriesGrouped(pageIndex).isEmpty)
        None
      else {
        Some(BlogEntryList(entriesGrouped(pageIndex), getPagination(page, entriesGrouped.size, pager)))
      }
    }
  }

  private def getPagination(current: Int, max: Int, pager: Int => Call) = {
    val (first, prev) = if (current > 1) (Some(1), Some(current - 1)) else (None, None)
    val (next, last) = if (current < max) (Some(current + 1), Some(max)) else (None, None)
    Pagination(first, prev, current, max, next, last, pager)
  }

}