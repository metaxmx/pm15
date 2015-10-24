package viewmodels

case class Pagination(first: Option[Int], prev: Option[Int], current: Int, next: Option[Int], last: Option[Int])