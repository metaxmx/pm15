package util.renderers.post

import org.jsoup.Jsoup
import scala.collection.convert.wrapAsScala._
import util.renderers.ContentWithAbstract
import util.renderers.RenderContext

object ImageGalleryPostRenderer extends PostRenderer {

  val boxPattern = """\{box\}(.*?)\{/?box\}""".r
  
  val galleryPattern = """\{gallery\}(.*?)\{/?gallery\}""".r

  override def render(content: ContentWithAbstract)(implicit context: RenderContext) =
    ContentWithAbstract(wrapImageBoxesAndGallery(content.abstractText), wrapImageBoxesAndGallery(content.content))

  def wrapImageBoxesAndGallery(content: String)(implicit context: RenderContext): String = {
    val replacedBoxElems = boxPattern.replaceAllIn(content, {
      matcher => s"""<div class="image_box">${matcher.group(1)}</div>"""
    })
    val replacedGalleryElems = galleryPattern.replaceAllIn(replacedBoxElems, {
      matcher => s"""<div class="image_gallery">${matcher.group(1)}</div>"""
    })
    
    val doc = Jsoup parseBodyFragment replacedGalleryElems
    doc.body.select("div.image_box img[title]").foreach {
      img =>
        val title = img.attr("title")
        val titleElem = doc.createElement("span").addClass("image_subtitle").text(title)
        img.after(titleElem)
    }
    doc.body.select("div.image_gallery img").foreach {
      img =>
        val imageContainer = doc.createElement("div").addClass("image_container")
        val imageContent = doc.createElement("div").addClass("image_content")
        img.after(imageContainer)
        imageContainer.appendChild(imageContent)
        imageContent.appendChild(img)
        if (img.hasAttr("title") && !img.attr("title").isEmpty()) {
          val imageTitle = doc.createElement("div").addClass("image_title")
          imageContainer.appendChild(imageTitle)
          imageTitle.text(img.attr("title"))
        }
    }
    doc.body.select("div.image_gallery br").foreach {
      _.remove()
    }
    doc.body.select("div.image_gallery .image_container:nth-child(2n)").foreach {
      img =>
        val split = doc.createElement("div").addClass("image_gallery_split")
        img.after(split)
    }
    doc.body.html
  }

}