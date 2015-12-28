package util.renderers.post

import scala.util.Try
import util.renderers.ContentWithAbstract
import util.renderers.RenderContext
import util.renderers.RenderContext
import util.renderers.RenderContext

trait PostRenderer {

  def include(implicit context: RenderContext): Boolean = true

  def render(content: ContentWithAbstract)(implicit context: RenderContext): ContentWithAbstract

}

object PostRenderers {

  val postRenderers: Seq[PostRenderer] =
    ReadAllPostRenderer ::
      CodeHighlightingPostRenderer ::
      ExternalLinksPostRenderer ::
      AttachmentImageUrlPostRenderer ::
      TableOfContentsPostRenderer ::
      ImageGalleryPostRenderer ::
      ImageResizePostRenderer ::
      Nil

  def postRender(renderedContent: ContentWithAbstract)(implicit context: RenderContext): Try[ContentWithAbstract] = Try {
    postRenderers.filter(_.include).foldLeft(renderedContent)((content, postRenderer) => postRenderer render content)
  }

}