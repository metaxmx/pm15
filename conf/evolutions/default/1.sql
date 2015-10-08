# Add Static Pages and Blog Entries
 
# --- !Ups

CREATE TABLE `static` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT "Primary key",
    `url` VARCHAR(255) COLLATE utf8_bin NOT NULL COMMENT "Page URL",
    `title` VARCHAR(1024) COLLATE utf8_bin NOT NULL COMMENT "Page Title",
    `content` LONGTEXT NOT NULL COMMENT "Page Content",
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT "Static Page table";

CREATE TABLE `blog` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT "Primary key",
    `url` VARCHAR(255) COLLATE utf8_bin NOT NULL COMMENT "Blog Title in URL format",
    `title` VARCHAR(1024) COLLATE utf8_bin NOT NULL COMMENT "Blog entry Title",
    `content` LONGTEXT NOT NULL COMMENT "Entry Content (raw)",
    `content_rendered` LONGTEXT NOT NULL COMMENT "Entry Content (rendered as HTML)",
    `published` BIT(1) NOT NULL COMMENT "Blog Entry published?",
    `published_date` DATETIME DEFAULT NULL COMMENT "Blog Entry Publishing Date",
    PRIMARY KEY (`id`)
)

# --- !Downs

DROP TABLE `static`;

DROP TABLE `blog`;
