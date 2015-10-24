package viewmodels

case class BlogEntryData(blogEntries: Seq[BlogEntryWithMeta]) {

  def published = BlogEntryData(blogEntries filter (_.blogEntry.published))

  def withCategory(url: String) = BlogEntryData(blogEntries filter (_.category.url == url))

  def withTag(url: String) = BlogEntryData(blogEntries filter (_.tags.exists(_.url == url)))

  def withYear(year: Int) = BlogEntryData(blogEntries filter (_.blogEntry.publishedDate.exists(_.getYear == year)))

  def withMonth(year: Int, month: Int) =
    BlogEntryData(blogEntries filter (_.blogEntry.publishedDate.exists(date => date.getYear == year && date.getMonthOfYear == month)))

}