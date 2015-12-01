package controllers

import play.api.mvc._
import scala.concurrent.Future
import util.exception.PageException
import util.exception.PageExceptions._
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.http.HeaderNames.AUTHORIZATION
import play.api.http.HeaderNames.WWW_AUTHENTICATE
import play.api.Configuration
import play.api.Play.current
import org.apache.commons.codec.binary.Base64
import com.typesafe.config.ConfigException

trait AbstractController extends Controller {

  class PageActionBuilder extends ActionBuilder[Request] {

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] =
      try {
        block(request) recoverWith {
          case pageExc: PageException => handleError(pageExc, request)
          case thr: Throwable         => handleInternalError(thr, request)
        }
      } catch {
        case pageExc: PageException => handleError(pageExc, request)
        case thr: Throwable         => handleInternalError(thr, request)
      }

    def handleInternalError[A](thr: Throwable, request: Request[A]) = {
      Logger.error(s"Error during page request ${request.path}", thr)
      handleError(pageInternalException, request)
    }

    def handleError[A](pageExc: PageException, request: Request[A]) = {
      Future successful pageExc.responseHandler(request)
    }

  }

  object PageAction extends PageActionBuilder

  class AdminActionBuilder extends PageActionBuilder {

    lazy val basicrealm = current.configuration.getString("admin.realm") getOrElse { throw new ConfigException.Missing("admin.realm") }
    lazy val username = current.configuration.getString("admin.username") getOrElse { throw new ConfigException.Missing("admin.username") }
    lazy val password = current.configuration.getString("admin.password") getOrElse { throw new ConfigException.Missing("admin.password") }

    private lazy val expectedHeaderValues = {
      val combined = username + ":" + password
      val credentials = Base64.encodeBase64String(combined.getBytes)
      basic(credentials)
    }

    private def realm = basic(s"""realm=\"${basicrealm}"""")

    private def basic(content: String) = s"Basic $content"

    private def unauthorized[A](request: Request[A]): Future[Result] =
      Future successful pageUnauthorizedException.responseHandler(request).withHeaders(WWW_AUTHENTICATE -> realm)

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] =
      request.headers.get(AUTHORIZATION) match {
        case Some(this.expectedHeaderValues) => super.invokeBlock(request, block)
        case _                               => unauthorized(request)
      }

  }

  object AdminAction extends AdminActionBuilder

}