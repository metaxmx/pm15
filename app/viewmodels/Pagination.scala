package viewmodels

import play.api.mvc.Call

case class Pagination(first: Option[Int], prev: Option[Int], current: Int, max: Int, next: Option[Int], last: Option[Int], pager: Int => Call) {

  def pageUrl(page: Int) = pager(page)

  def visible = prev.isDefined || next.isDefined

}