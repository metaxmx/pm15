package controllers

import javax.inject.{ Inject, Singleton }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Call
import play.api.mvc.Request
import services.BlogService
import util.exception.PageExceptions
import models.BlogEntry
import viewmodels.BlogEntryWithMeta
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTimeZone
import java.util.Locale
import scala.xml.PCData
import scala.xml.PrettyPrinter

@Singleton
class RssController @Inject() (blogService: BlogService) extends AbstractController {

  case class RssChannel(
    title: String,
    link: String,
    description: String,
    language: String,
    copyright: String,
    generator: String,
    lastBuild: String)

  def showRss = PageAction.async {
    implicit request =>
      val channel = RssChannel(
        "Planet Metax - Weblog-Einträge",
        routes.BlogController.blogOverview.absoluteURL(),
        "Einträge im Weblog von Planet Metax",
        "de-de",
        "Christian Simon",
        "Planet Metax Engine '15",
        formatDate(DateTime.now()))
      blogService.getListWithMeta() map {
        entries =>
          val entriesPublished = entries.blogEntries filter {
            entry =>
              entry.blogEntry.published && entry.blogEntry.publishedDate.isDefined
          } take 10
          Ok(rssContent(channel, entriesPublished)).as("application/xhtml+xml")
      }
  }

  private val dateFormatter = DateTimeFormat.forPattern("d MMM yyyy HH:mm:ss 'GMT'").withLocale(Locale.US)

  private val dateTimeZone = DateTimeZone.forID("GMT")

  private def formatDate(date: DateTime): String = dateFormatter print date.withZone(dateTimeZone)

  private def rssContent(channel: RssChannel, entries: Seq[BlogEntryWithMeta])(implicit request: Request[_]): String = {
    val channelXml =
      <channel>
        <title>{ channel.title }</title>
        <link>{ channel.link }</link>
        <description>{ channel.description }</description>
        <language>{ channel.language }</language>
        <copyright>{ channel.copyright }</copyright>
        <generator>{ channel.generator }</generator>
        <lastBuildDate>{ channel.lastBuild }</lastBuildDate>
        {
          entries map (rssItem(_))
        }
      </channel>
    val rssXml = <rss version="2.0">
                   { channelXml }
                 </rss>
    val printer = new PrettyPrinter(80, 2)
    """<?xml version="1.0" encoding="UTF-8" ?>""" + "\n" + printer.format(rssXml)
  }

  private def rssItem(entry: BlogEntryWithMeta)(implicit request: Request[_]) = {
    val url = routes.BlogController.showBlogEntry(entry.blogEntry.url).absoluteURL()
    <item>
      <title>{ entry.blogEntry.title }</title>
      <link>{ url }</link>
      <description>
        { new PCData(entry.blogEntry.abstractRendered) }
      </description>
      <pubDate>{ formatDate(entry.blogEntry.publishedDate.get) }</pubDate>
      <guid isPermaLink="true">{ url }</guid>
    </item>
  }

}