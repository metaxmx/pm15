package util.renderers

import java.io.File
import scala.util.Try
import controllers.routes
import models.BlogEntry
import play.api.mvc.Call
import util.renderers.post.PostRenderers
import util.renderers.pre.PreRenderers
import models.Attachment

case class ContentWithAbstract(abstractText: String, content: String) {

  def map(f: String => String) = ContentWithAbstract(f(abstractText), f(content))

}

sealed trait RenderType
case object RenderTypeBlog extends RenderType

case class RenderContext(renderType: RenderType, format: String, attachments: Seq[Attachment], attachmentFolder: Option[File],
                         call: Call, attachmentCall: String => Call, galleryAttachmentCall: String => Call,
                         boxAttachmentCall: String => Call, thumbnailAttachmentCall: String => Call)

object RenderContext {

  val blogAttachmentRoot = new File("media/blog")

  def blogAttachmentDestination(blogId: Int) = new File(blogAttachmentRoot, blogId.toString)

  def blogAttachmentFolder(blogId: Int) = Option(blogAttachmentDestination(blogId)) filter { _.isDirectory }

  def blogRenderContext(blog: BlogEntry, attachments: Seq[Attachment]): RenderContext =
    RenderContext(RenderTypeBlog, blog.contentFormat, attachments,
    blogAttachmentFolder(blog.id), routes.BlogController.showBlogEntry(blog.url),
    routes.BlogController.attachment(blog.url, _), routes.BlogController.attachmentGallery(blog.url, _),
    routes.BlogController.attachmentBox(blog.url, _), routes.BlogController.attachmentStandalone(blog.url, _))

}

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