package util.renderers

import scala.util.Try
import util.renderers.post.PostRenderers
import java.io.File
import util.renderers.pre.PreRenderers
import play.api.mvc.Call

case class ContentWithAbstract(abstractText: String, content: String)

sealed trait RenderType
case object RenderTypeBlog extends RenderType

case class RenderContext(renderType: RenderType, format: String, attachments: Option[File], call: Call, attachmentCall: String => Call)

trait ContentRenderer {

  def renderFormat: String

  def render(source: String)(implicit context: RenderContext): ContentWithAbstract

}

object ContentRenderers {

  val contentRenderers: Seq[ContentRenderer] =
    MarkdownContentRenderer :: HtmlContentRenderer :: Nil

  val contentRenderersByFormat = contentRenderers.map { r => r.renderFormat -> r }.toMap

  def render(source: String)(implicit context: RenderContext): Option[Try[ContentWithAbstract]] = {
    val format = context.format
    val sourcePre = PreRenderers preRender source
    contentRenderersByFormat get format map {
      contentRenderer => sourcePre flatMap { s => Try(contentRenderer render s) }
    } map {
      _ flatMap {
        PostRenderers postRender _
      }
    }
  }

}