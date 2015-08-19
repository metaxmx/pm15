package services

import models.StaticPage
import javax.inject.Singleton

@Singleton
class StaticPageService {

  // Dummy content until the database is set up

  val imprintContent =
    <h2>Impressum</h2>
    <p>Verantwortlich fuer diese Internetpraesenz:</p>
    <p>
      Christian Simon<br/>
      Gertrud-von-le-Fort-Str. 4
      97074 Wuerzburg
    </p>

  val staticPages = Seq(
    StaticPage(1, "impressum", "Impressum", imprintContent.mkString))
  val staticPagesByUrl = staticPages.map { sp => sp.url -> sp }.toMap

  // Dummy
  def getStaticPage(url: String): Option[StaticPage] = staticPagesByUrl get url

}