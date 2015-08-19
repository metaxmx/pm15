package controllers

import play.api.mvc._
import scala.concurrent.Future
import util.exception.PageException
import util.exception.PageExceptions._
import play.api.Logger
import util.exception.PageExceptions._

trait AbstractController extends Controller {

  object PageAction extends ActionBuilder[Request] {

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] =
      try {
        block(request)
      } catch {
        case pageExc: PageException => handleError(pageExc, request)
        case thr: Throwable => {
          Logger.error(s"Error during page request ${request.path}", thr)
          handleError(pageInternalException, request)
        }
      }

    def handleError[A](pageExc: PageException, request: Request[A]) = {
      Future.successful(pageExc.responseHandler(request))
    }

  }
}