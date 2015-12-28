package util.renderers.post

import org.jsoup.Jsoup
import scala.collection.convert.wrapAsScala._
import util.renderers.ContentWithAbstract
import util.renderers.RenderContext
import org.jsoup.nodes.Element
import scala.collection.mutable.ListBuffer

object TableOfContentsPostRenderer extends PostRenderer {

  val tocToken = "[toc]"

  override def include(implicit context: RenderContext) = true

  override def render(content: ContentWithAbstract)(implicit context: RenderContext) = {
    val ContentWithAbstract(abstractText, contentRendered) = content map giveHeadlinesIds
    if ((abstractText contains tocToken) || (contentRendered contains tocToken)) {
      val outline = genOutline(contentRendered)
      if (!outline.entries.isEmpty) {
        ContentWithAbstract(
          if (abstractText contains tocToken) abstractText.replace(tocToken, genToc(outline, true)) else abstractText,
          if (contentRendered contains tocToken) contentRendered.replace(tocToken, genToc(outline, false)) else contentRendered)
      } else {
        ContentWithAbstract(abstractText.replace(tocToken, ""), contentRendered.replace(tocToken, ""))
      }
    } else {
      // Table of Content not included - don't generate
      content
    }
  }

  private def giveHeadlinesIds(content: String): String = {
    val doc = Jsoup parseBodyFragment content
    doc.getAllElements.filter { Set("h1", "h2", "h3", "h4", "h5", "h6") contains _.tagName.toLowerCase }.foreach {
      head =>
        if (head.id() != null) {
          val id = head.text.toLowerCase.replaceAll("[^a-z0-9]+", "-").replaceAll("--+", "").replaceAll("^-", "").replaceAll("-$", "")
          val idLimited = if (id.length > 50) id.substring(0, 50) else id
          head.attr("id", idLimited)
        }
    }
    doc.body.html
  }

  case class Outline(entries: Seq[OutlineEntry])

  case class OutlineEntry(title: String, fragment: Option[String], subEntries: Seq[OutlineEntry])

  case class MutableOutlineEntry(title: String, fragment: Option[String], level: Int, subEntries: ListBuffer[MutableOutlineEntry])

  private def genOutline(content: String): Outline = {
    val doc = Jsoup parseBodyFragment content
    val entryBuilder = ListBuffer[MutableOutlineEntry]()
    def appendInLevel(title: String, fragment: Option[String], level: Int, builder: ListBuffer[MutableOutlineEntry]): Unit = {
      val lastOpt = builder.lastOption
      if (lastOpt.isEmpty || lastOpt.get.level >= level) {
        builder += MutableOutlineEntry(title, fragment, level, ListBuffer())
      } else {
        appendInLevel(title, fragment, level, lastOpt.get.subEntries)
      }
    }
    doc.getAllElements.filter { Set("h1", "h2", "h3", "h4", "h5", "h6") contains _.tagName.toLowerCase }.foreach {
      header =>
        val title = header.text()
        val level = header.tagName().substring(1).toInt
        val fragment = Option(header.id)
        appendInLevel(title, fragment, level, entryBuilder)
    }
    def mapToOutline(mutable: MutableOutlineEntry): OutlineEntry =
      OutlineEntry(mutable.title, mutable.fragment, mutable.subEntries map mapToOutline)
    Outline(entryBuilder map mapToOutline)
  }

  private def genToc(outline: Outline, forAbstract: Boolean)(implicit context: RenderContext): String = {
    val doc = Jsoup.parseBodyFragment("""<div class="blog_toc"><p class="toc_title">Inhaltsverzeichnis</p></div>""")
    val list = doc.body().child(0).appendElement("ul")
    def genEntriesRec(ul: Element, entries: Seq[OutlineEntry]): Unit = {
      entries.foreach {
        entry =>
          val li = ul.appendElement("li")
          if (entry.fragment.isDefined) {
            val hrefFromAbstract = context.call.withFragment(entry.fragment.get).toString
            val hrefFromFullpage = "#" + entry.fragment.get
            li.appendElement("a").attr("href", if (forAbstract) hrefFromAbstract else hrefFromFullpage).text(entry.title)
          } else {
            li.text(entry.title)
          }
          if (!entry.subEntries.isEmpty) {
            val subUl = li.appendElement("ul")
            genEntriesRec(subUl, entry.subEntries)
          }
      }
    }
    genEntriesRec(list, outline.entries)
    doc.body.html
  }

}