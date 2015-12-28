package models

import AttachmentTypes.{ AttachmentColumnType, AttachmentType }
import slick.driver.MySQLDriver.api._
import slick.lifted.{ Tag => SlickTag }

object AttachmentTypes {

  sealed trait AttachmentType
  case object HiddenAttachment extends AttachmentType {
    val identifier = "hidden"
  }
  case object InlineAttachment extends AttachmentType {
    val identifier = "inline"
  }
  case object DownloadAttachment extends AttachmentType {
    val identifier = "download"
  }

  implicit val AttachmentColumnType = MappedColumnType.base[AttachmentType, String](
    _ match {
      case HiddenAttachment   => HiddenAttachment.identifier
      case InlineAttachment   => InlineAttachment.identifier
      case DownloadAttachment => DownloadAttachment.identifier
    },
    _ match {
      case HiddenAttachment.identifier   => HiddenAttachment
      case InlineAttachment.identifier   => InlineAttachment
      case DownloadAttachment.identifier => DownloadAttachment
    })

}

object AttachmentImageFormat {

  sealed abstract class ImageFormat(
      val folder: String,
      val width: Int,
      val height: Int) {

    def fitInside(imgWidth: Int, imgHeight: Int) = imgWidth <= width && imgHeight <= height

  }
  case object GalleryImageFormat extends ImageFormat(folder = "gallery", width = 380, height = 400)
  case object BoxImageFormat extends ImageFormat(folder = "box", width = 890, height = 600)
  case object FullSizeImageFormat extends ImageFormat(folder = "thumb", width = 910, height = 600)

}

case class Attachment(
  id: Int,
  blogId: Int,
  url: String,
  filename: Option[String],
  attachmentType: AttachmentType,
  mime: String,
  downloads: Int) extends KeyedEntity

class AttachmentTable(tag: SlickTag)
    extends Table[Attachment](tag, "attachment") with KeyedEntityTable {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def blogId = column[Int]("blog_id")
  def url = column[String]("url")
  def filename = column[Option[String]]("filename")
  def attachmentType = column[AttachmentType]("attachment_type")
  def mime = column[String]("mime")
  def downloads = column[Int]("downloads")

  def blogEntry = foreignKey("fk_blogattachment_blogentry", blogId, BlogEntries)(_.id)

  def * = (id, blogId, url, filename, attachmentType, mime, downloads) <> (Attachment.tupled, Attachment.unapply)
}

object Attachments extends TableQuery(new AttachmentTable(_)) with BaseTableQuery
