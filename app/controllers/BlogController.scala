package controllers

import javax.inject.Singleton
import javax.inject.Inject
import services.StaticPageService
import play.api.mvc._
import util.exception.PageExceptions
import play.api.libs.concurrent.Execution.Implicits._
import services.BlogService

@Singleton
class BlogController @Inject() (blogService: BlogService) extends AbstractController {
  
}