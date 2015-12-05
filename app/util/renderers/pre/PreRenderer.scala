package util.renderers.pre

import scala.util.Try
import java.io.File
import util.renderers.RenderContext

trait PreRenderer {

  def include(implicit context: RenderContext): Boolean

  def render(content: String)(implicit context: RenderContext): String

}

object PreRenderers {

  val preRenderers: Seq[PreRenderer] = IncludePrePrenderer :: Nil

  def preRender(content: String)(implicit context: RenderContext): Try[String] = Try {
    preRenderers.filter(_.include).foldLeft(content)((content, preRenderer) => preRenderer render content)
  }

}