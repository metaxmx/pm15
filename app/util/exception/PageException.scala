package util.exception

import play.api.mvc.Request
import play.api.mvc.Results._
import play.api.mvc.AnyContent
import play.api.mvc._
import controllers.routes

case class PageException(responseHandler: Request[_] => Result) extends RuntimeException

object PageExceptions {

  val pageNotFoundException = PageException { request => NotFound(views.html.error_notfound(request.path)) }
  val pageForbiddenException = PageException { request => Forbidden(views.html.error_forbidden()) }
  val pageBadRequestException = PageException { request => BadRequest(views.html.error_badrequest()) }
  val pageInternalException = PageException { request => InternalServerError(views.html.error_internalerror()) }

}
