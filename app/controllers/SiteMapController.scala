package controllers

import javax.inject.Singleton
import play.api.mvc.Call
import play.api.mvc.Request
import play.api.mvc.RequestHeader
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import scala.xml.PrettyPrinter
import javax.inject.Inject
import services.BlogService
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@Singleton
class SiteMapController @Inject() (blogService: BlogService) extends AbstractController {

  object ChangeFreq {
    val monthly = "monthly"
    val weekly = "weekly"
    val daily = "daily"
  }

  private val dateFormatter = ISODateTimeFormat.dateTimeNoMillis()

  case class SiteMapUrl(
      page: Call,
      changefreq: String,
      changed: Option[DateTime],
      priority: String) {

    def loc(implicit req: RequestHeader) = page.absoluteURL()

    def lastmod = changed map (dateFormatter print _)

  }

  def showSiteMap = PageAction.async {
    implicit request =>
      genSitemapUrls map {
        urls =>
          Ok(sitemapContent(urls)).as("text/xml;charset=UTF-8")
      }
  }

  def sitemapContent(urls: Seq[SiteMapUrl])(implicit req: RequestHeader): String = {
    val xmlData =
      <urlset xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd" xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
        {
          urls map {
            url =>
              <url>
                <loc>{ url.loc }</loc>
                <changefreq>{ url.changefreq }</changefreq>
                {
                  if (url.changed.isDefined) {
                    <changed>{ url.lastmod.get }</changed>
                  }
                }
                <priority>{ url.priority }</priority>
              </url>
          }
        }
      </urlset>
    val printer = new PrettyPrinter(80, 2)
    """<?xml version="1.0" encoding="UTF-8" ?>""" + "\n" + printer.format(xmlData)
  }

  def genSitemapUrls: Future[Seq[SiteMapUrl]] =
    blogService.getAll() map { blogEntries =>
      (SiteMapUrl(routes.StaticPageController.showIndexPage(), ChangeFreq.monthly, None, "0.5") ::
        SiteMapUrl(routes.BlogController.blogOverview(), ChangeFreq.daily, None, "1.0") ::
        SiteMapUrl(routes.StaticPageController.showProfilPage(), ChangeFreq.weekly, None, "1.0") ::
        SiteMapUrl(routes.StaticPageController.showKontaktPage(), ChangeFreq.weekly, None, "0.8") ::
        SiteMapUrl(routes.StaticPageController.showImpressumPage(), ChangeFreq.monthly, None, "0.1") ::
        SiteMapUrl(routes.StaticPageController.showDatenschutzPage(), ChangeFreq.monthly, None, "0.1") :: Nil) ++
        blogEntries.filter { _.publishedDate.isDefined }.map {
          blogEntry =>
            SiteMapUrl(routes.BlogController.showBlogEntry(blogEntry.url), ChangeFreq.monthly,
              Some(blogEntry.publishedDate.get), "1.0")
        }
    }

}