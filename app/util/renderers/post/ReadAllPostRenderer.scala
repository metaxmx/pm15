package util.renderers.post

import org.jsoup.Jsoup
import scala.collection.convert.wrapAsScala._
import util.renderers.ContentWithAbstract
import util.renderers.RenderContext
import util.renderers.RenderTypeBlog

object ReadAllPostRenderer extends PostRenderer {

  override def include(implicit context: RenderContext) = context.renderType == RenderTypeBlog

  override def render(content: ContentWithAbstract)(implicit context: RenderContext) = {
    val abstractDoc = Jsoup parseBodyFragment content.abstractText
    val fullContentDoc = Jsoup parseBodyFragment content.content
    if (abstractDoc.body.text.length < fullContentDoc.body.text.length) {
      // Abstract does not contain full content
      abstractDoc.body.appendElement("div").addClass("read-more")
        .appendElement("a").attr("href", context.call.path).addClass("btn").addClass("btn-primary").text("Den kompletten Artikel lesen")
      ContentWithAbstract(abstractDoc.body.html, content.content)
    } else {
      content
    }
  }

}