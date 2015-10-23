package viewmodels

import models._

case class BlogEntryWithMeta(
    blogEntry: BlogEntry,
    category: Category,
    tags: Seq[Tag])