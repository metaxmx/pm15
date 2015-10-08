package controllers

import play.api.mvc._
import scala.concurrent.Future
import util.exception.PageException
import util.exception.PageExceptions._
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._

trait AbstractController extends Controller {

  object PageAction extends ActionBuilder[Request] {

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
      Future.successful(pageExc.responseHandler(request))
    }

  }
}