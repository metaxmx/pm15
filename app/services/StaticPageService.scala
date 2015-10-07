package services

import models.StaticPage
import javax.inject.Singleton

@Singleton
class StaticPageService {

  // Dummy content until the database is set up

  val startPageContent =
    <h1>Start</h1>
    <p>Willkommen auf Planet Metax</p>

  val artikelContent =
    <h1>Artikel</h1>
    <p>Hier entsteht bald eine Seite</p>

  val profilContent =
    <h1>Profil</h1>
    <p>Hier entsteht bald eine Seite</p>

  val kontaktContent =
    <h1>Kontakt</h1>
    <p>Hier entsteht bald eine Seite</p>

  val impressumContent =
    <h1>Impressum</h1>
    <p>Verantwortlich fuer diese Internetpraesenz:</p>
    <p>
      Christian Simon<br/>
      Gertrud-von-le-Fort-Str. 4
  97074 Wuerzburg
    </p>

  val datenschutzContent =
    <h1>Datenschutzvereinbarung</h1>
    <p>Hier entsteht bald eine Seite</p>

  val faqContent =
    <h1>FAQ</h1>
    <p>Hier entsteht bald eine Seite</p>

  private var dummyId: Int = 0

  private def nextDummyId = {
    dummyId += 1
    dummyId
  }

  val staticPages = Seq(
    StaticPage(nextDummyId, "index", "Start", startPageContent.mkString),
    StaticPage(nextDummyId, "artikel", "Artikel", artikelContent.mkString),
    StaticPage(nextDummyId, "profil", "Profil", profilContent.mkString),
    StaticPage(nextDummyId, "kontakt", "Kontakt", kontaktContent.mkString),
    StaticPage(nextDummyId, "impressum", "Impressum", impressumContent.mkString),
    StaticPage(nextDummyId, "datenschutz", "Datenschutzvereinbarung", datenschutzContent.mkString),
    StaticPage(nextDummyId, "faq", "FAQ", faqContent.mkString))
  val staticPagesByUrl = staticPages.map { sp => sp.url -> sp }.toMap

  println(staticPagesByUrl)

  // Dummy
  def getStaticPage(url: String): Option[StaticPage] = staticPagesByUrl get url

}