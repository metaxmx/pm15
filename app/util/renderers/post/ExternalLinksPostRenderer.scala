package util.renderers.post

import org.jsoup.Jsoup
import scala.collection.convert.wrapAsScala._
import util.renderers.ContentWithAbstract
import util.renderers.RenderContext

object ExternalLinksPostRenderer extends PostRenderer {

  override def include(implicit context: RenderContext) = true

  override def render(content: ContentWithAbstract)(implicit context: RenderContext) =
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