package util.renderers.post

import java.io.File

import scala.collection.convert.wrapAsScala.asScalaBuffer

import org.apache.commons.io.FilenameUtils
import org.jsoup.Jsoup

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.GifWriter
import com.sksamuel.scrimage.nio.ImageWriter
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.nio.PngWriter

import models.AttachmentImageFormat.BoxImageFormat
import models.AttachmentImageFormat.FullSizeImageFormat
import models.AttachmentImageFormat.GalleryImageFormat
import util.Logging
import util.renderers.ContentWithAbstract
import util.renderers.RenderContext

object ImageResizePostRenderer extends PostRenderer with Logging {

  override def render(content: ContentWithAbstract)(implicit context: RenderContext) = content map resizeImages

  def resizeImages(content: String)(implicit context: RenderContext) = {
    val doc = Jsoup parseBodyFragment content
    doc.body.select("img").foreach {
      img =>
        val (targetFormat, thumbCall) = if (img.parents().hasClass("image_box")) {
          (BoxImageFormat, context.boxAttachmentCall)
        } else if (img.parents().hasClass("image_gallery")) {
          (GalleryImageFormat, context.galleryAttachmentCall)
        } else {
          (FullSizeImageFormat, context.thumbnailAttachmentCall)
        }
        val filename = FilenameUtils.getName(img.attr("src"))
        val imageFileOpt = for {
          attachment <- context.attachments.find { att => att.url == filename || att.filename == filename }
          parentFolder <- context.attachmentFolder
          file <- Option(new File(parentFolder, attachment.filename.getOrElse(attachment.url))) if file.exists
        } yield file
        if (imageFileOpt.isDefined) {
          val imageFile = imageFileOpt.get
          val imageFullsize = Image.fromFile(imageFile)
          val imageResized = if (targetFormat.fitInside(imageFullsize.width, imageFullsize.height)) {
            imageFullsize
          } else {
            val thumbFile = new File(new File(imageFile.getParentFile, targetFormat.folder), imageFile.getName)
            val imageResized = if (thumbFile.exists()) {
              log.info(s"Thumbnail for attachment $filename already exists.")
              Image.fromFile(thumbFile)
            } else {
              val resized = imageFullsize.bound(targetFormat.width, targetFormat.height)
              thumbFile.getParentFile.mkdirs()
              try {
                resized.output(thumbFile)(imageWrite(filename))
              } catch {
                case e: Throwable =>
                  log.error(s"Error writing $thumbFile", e)
                  e.printStackTrace()
                  throw e
              }
              log.info(s"Resized $filename from ${imageFullsize.width}x${imageFullsize.height} to ${resized.width}x${resized.height}")
              resized
            }
            img.attr("src", thumbCall(filename).url)
            if (img.parent().nodeName() != "a") {
              // Link to fullsize image, unless image is already linked
              val link = doc.createElement("a").attr("href", context.attachmentCall(filename).url).attr("target", "_blank")
              img.after(link)
              link.appendChild(img)
            }
            imageResized
          }
          img.attr("width", imageResized.width.toString)
          img.attr("height", imageResized.height.toString)
        } else {
          log.error(s"Attachment image file $filename not found.")
        }

    }
    doc.body.html
  }

  private def imageWrite(filename: String): ImageWriter = FilenameUtils.getExtension(filename).toLowerCase match {
    case "gif" => GifWriter.Progressive
    case "png" => PngWriter.MaxCompression
    case _     => JpegWriter().withCompression(100).withProgressive(true)
  }

}