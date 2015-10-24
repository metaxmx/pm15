package util

import org.slf4j.LoggerFactory
import play.api.Logger

trait Logging {

  val log = Logger.apply("application")

}