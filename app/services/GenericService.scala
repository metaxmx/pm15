package services

import util.exception.PageExceptions
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

trait GenericService {

  def require[A](maybeEntity: Option[A]): A = maybeEntity getOrElse (throw PageExceptions.pageNotFoundException)

  def require[A](maybeFuture: Future[Option[A]])(implicit ec: ExecutionContext): Future[A] = maybeFuture map { require(_) }

}