package viewmodels

case class BlogEntryList(blogEntries: Seq[BlogEntryWithMeta], pagination: Pagination)