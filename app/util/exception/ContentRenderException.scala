package util.exception

case class ContentRenderException(msg: String, e: Option[Exception] = None) extends Exception(msg, e getOrElse null)