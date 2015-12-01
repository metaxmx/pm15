package util.renderers

import java.lang.Boolean
import scala.util.Try

trait PostRenderer {

  def include(renderFormat: String): Boolean

  def render(content: ContentWithAbstract): ContentWithAbstract

}

object PostRenderers {

  val postRenderers: Seq[PostRenderer] = CodeHighlightingPostRenderer :: ExternalLinksPostRenderer :: Nil

  def postRender(renderedContent: ContentWithAbstract, format: String): Try[ContentWithAbstract] = Try {
    postRenderers.filter(_.include(format)).foldLeft(renderedContent)((content, postRenderer) => postRenderer render content)
  }

}