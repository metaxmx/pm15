package util.renderers

import scala.util.Try

case class ContentWithAbstract(abstractText: String, content: String)

trait ContentRenderer {

  def renderFormat: String

  def render(source: String): ContentWithAbstract

}

object ContentRenderers {

  val contentRenderers: Seq[ContentRenderer] = Seq(Pm09ContentRenderer, MarkdownContentRenderer, HtmlContentRenderer)

  val contentRenderersByFormat = contentRenderers.map { r => r.renderFormat -> r }.toMap

  def render(source: String, format: String): Option[Try[ContentWithAbstract]] = {
    contentRenderersByFormat get format map {
      contentRenderer => Try(contentRenderer render source)
    } map {
      _ flatMap {
        PostRenderers.postRender(_, format)
      }
    }
  }

}