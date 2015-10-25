# Add Default Static Pages
 
# --- !Ups

INSERT INTO `static` (`id`, `url`, `title`, `content`, `content_rendered`, `content_format`) VALUES
  (NULL, "index", "Start", "<h1>Start</h1>\n<p>Willkommen auf Planet Metax</p>", "", "md"),
  (NULL, "artikel", "Artikel", "<h1>Artikel</h1>\n<p>Hier entsteht bald eine Seite</p>", "", "md"),
  (NULL, "profil", "Profil", "<h1>Profile</h1>\n<p>Hier entsteht bald eine Seite</p>", "", "md"),
  (NULL, "kontakt", "Kontakt", "<h1>Kontakt</h1>\n<p>Hier entsteht bald eine Seite</p>", "", "md"),
  (NULL, "impressum", "Impressum", "<h1>Impressum</h1>\n<p>Verantwortlich fuer diese Internetpraesenz:</p>\n<p>Christian Simon<br/>Gertrud-von-le-Fort-Str. 4<br/>97074 Wuerzburg</p>", "", "md"),
  (NULL, "datenschutz", "Datenschutzvereibarung", "<h1>Datenschutzvereinbarung</h1><p>Hier entsteht bald eine Seite</p>", "", "md"),
  (NULL, "faq", "FAQ", "<h1>FAQ</h1><p>Hier entsteht bald eine Seite</p>", "", "md");

# --- !Downs

DELETE FROM `static`;