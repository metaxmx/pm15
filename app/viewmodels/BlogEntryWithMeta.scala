package viewmodels

import models._
import org.joda.time.DateTime

case class BlogEntryWithMeta(
  blogEntry: BlogEntry,
  category: Category,
  tags: Seq[Tag])

object BlogEntryWithMeta {

  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  implicit def orderBlogEntryWithMeta: Ordering[BlogEntryWithMeta] = Ordering.by { _.blogEntry }

}