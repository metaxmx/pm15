# Add Static Pages and Blog Entities
 
# --- !Ups

CREATE TABLE `static` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT "Primary key",
    `url` VARCHAR(255) COLLATE utf8_bin NOT NULL COMMENT "Page URL",
    `title` VARCHAR(1024) COLLATE utf8_bin NOT NULL COMMENT "Page Title",
    `content` LONGTEXT NOT NULL COMMENT "Page Content",
    `content_rendered` LONGTEXT NOT NULL COMMENT "Page Content (rendered as HTML)",
    `content_format` VARCHAR(255) NOT NULL COMMENT "Page Render Format",
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_static_unique_url` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT "Static Page table";

CREATE TABLE `category` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT "Primary key",
    `url` VARCHAR(255) COLLATE utf8_bin NOT NULL COMMENT "Category URL",
    `title` VARCHAR(1024) COLLATE utf8_bin NOT NULL COMMENT "Category Title",
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_category_unique_url` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT "Blog Category table";

CREATE TABLE `tag` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT "Primary key",
    `url` VARCHAR(255) COLLATE utf8_bin NOT NULL COMMENT "Tag URL",
    `title` VARCHAR(1024) COLLATE utf8_bin NOT NULL COMMENT "Tag Title",
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_tag_unique_url` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT "Blog Tag table";

CREATE TABLE `blog` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT "Primary key",
    `category_id` BIGINT(20) NOT NULL COMMENT "Foreign key to category",
    `url` VARCHAR(255) COLLATE utf8_bin NOT NULL COMMENT "Blog Title in URL format",
    `title` VARCHAR(1024) COLLATE utf8_bin NOT NULL COMMENT "Blog entry Title",
    `content` LONGTEXT NOT NULL COMMENT "Entry Content (raw)",
    `content_rendered` LONGTEXT NOT NULL COMMENT "Entry Content (rendered as HTML)",
    `abstract_rendered` LONGTEXT NOT NULL COMMENT "Entry Abstract (rendered as HTML)",
    `content_format` VARCHAR(255) NOT NULL COMMENT "Entry Render Format",
    `published` BIT(1) NOT NULL COMMENT "Blog Entry published?",
    `published_date` DATETIME DEFAULT NULL COMMENT "Blog Entry Publishing Date",
    `views` INT NOT NULL COMMENT "Number of page views",
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_blog_unique_url` (`url`),
    KEY `fk_blog_category` (`category_id`),
    CONSTRAINT `fk_blog_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT "Blog Entry table";

CREATE TABLE `blog_has_tag` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT "Primary key",
    `blog_id` BIGINT(20) NOT NULL COMMENT "Foreign key to blog entry",
    `tag_id` BIGINT(20) NOT NULL COMMENT "Foreign key to tag",
    PRIMARY KEY (`id`),
    KEY `fk_bloghastag_blog` (`blog_id`),
    KEY `fk_bloghastag_tag` (`tag_id`),
    CONSTRAINT `fk_bloghastag_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`id`),
    CONSTRAINT `fk_bloghastag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT "Blog-has-Tag table";

CREATE TABLE `attachment` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT "Primary key",
    `blog_id` BIGINT(20) NOT NULL COMMENT "Foreign key to blog entry",
    `url` VARCHAR(255) COLLATE utf8_bin NOT NULL COMMENT "Attachment URL",
    `filename` VARCHAR(1024) COLLATE utf8_bin NOT NULL COMMENT "Attachment filename",
    `attachment_type` VARCHAR(255) COLLATE utf8_bin NOT NULL COMMENT "Attachment type",
    `mime` VARCHAR(255) COLLATE utf8_bin NOT NULL COMMENT "MIME type",
    `downloads` INT NOT NULL COMMENT "Number of downloads",
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_attachment_unique_blogid_url` (`blog_id`, `url`),
    KEY `fk_attachment_blog` (`blog_id`),
    CONSTRAINT `fk_attachment_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
COMMENT "Blog Attachment table";

# --- !Downs

DROP TABLE `attachment`;

DROP TABLE `blog_has_tag`;

DROP TABLE `blog`;

DROP TABLE `tag`;

DROP TABLE `category`;

DROP TABLE `static`;
