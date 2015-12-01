package util.renderers

import org.jsoup.Jsoup
import scala.collection.convert.decorateAsScala._
import scala.collection.convert.wrapAsScala._
import util.Logging

object ExternalLinksPostRenderer extends PostRenderer with Logging {

  override def include(renderFormat: String) = true

  override def render(content: ContentWithAbstract) =
    ContentWithAbstract(replaceExternalLinks(content.abstractText), replaceExternalLinks(content.content))

  def replaceExternalLinks(content: String): String = {
    val doc = Jsoup parseBodyFragment content
    doc.body.select("a[href]").foreach {
      link =>
        val href = link.attr("href")
        if ((href startsWith "http://") || (href startsWith "https://")) {
          if (!link.hasAttr("target") || link.attr("target") != "_blank") {
            link.attr("target", "_blank")
          }
        }
    }
    doc.body.html
  }

}