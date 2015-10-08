# Add Default Static Pages
 
# --- !Ups

INSERT INTO `static` (`id`, `url`, `title`, `content`) VALUES
  (NULL, "index", "Start", "<h1>Start</h1>\n<p>Willkommen auf Planet Metax</p>"),
  (NULL, "artikel", "Artikel", "<h1>Artikel</h1>\n<p>Hier entsteht bald eine Seite</p>"),
  (NULL, "profil", "Profil", "<h1>Profile</h1>\n<p>Hier entsteht bald eine Seite</p>"),
  (NULL, "kontakt", "Kontakt", "<h1>Kontakt</h1>\n<p>Hier entsteht bald eine Seite</p>"),
  (NULL, "impressum", "Impressum", "<h1>Impressum</h1>\n<p>Verantwortlich fuer diese Internetpraesenz:</p>\n<p>Christian Simon<br/>Gertrud-von-le-Fort-Str. 4<br/>97074 Wuerzburg</p>"),
  (NULL, "datenschutz", "Datenschutzvereibarung", "<h1>Datenschutzvereinbarung</h1><p>Hier entsteht bald eine Seite</p>"),
  (NULL, "faq", "FAQ", "<h1>FAQ</h1><p>Hier entsteht bald eine Seite</p>");

# --- !Downs

DELETE FROM `static`;