package util.renderers.post

import org.jsoup.Jsoup
import scala.collection.convert.wrapAsScala._
import util.renderers.ContentWithAbstract
import util.renderers.RenderContext

object AttachmentImageUrlPostRenderer extends PostRenderer {

  override def include(implicit context: RenderContext) = true

  override def render(content: ContentWithAbstract)(implicit context: RenderContext) =
    ContentWithAbstract(fixAttachmentImageUrls(content.abstractText), fixAttachmentImageUrls(content.content))

  def fixAttachmentImageUrls(content: String)(implicit context: RenderContext): String = {
    val doc = Jsoup parseBodyFragment content
    doc.body.select("img[src]").foreach {
      img =>
        val src = img.attr("src")
        if (!src.contains("/")) {
          img.attr("src", context.attachmentCall(src).path)
        }
    }
    doc.body.html
  }

}