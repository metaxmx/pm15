package util

import play.api.libs.json.Reads
import play.api.libs.json.JsValue

object JsonExtraction {

  class Extractor[T](implicit reads: Reads[T]) {

    def unapply(value: JsValue): Option[T] = value.asOpt[T]

  }

  def extract[T](implicit reads: Reads[T]) = new Extractor[T]

}