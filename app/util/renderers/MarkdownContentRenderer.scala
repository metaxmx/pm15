package util.renderers

import org.pegdown.PegDownProcessor
import org.pegdown.Extensions

object MarkdownContentRenderer extends ContentRenderer {

  override val renderFormat = "md"

  def MAX_PARSING_TIME = 20000

  def EXTENSIONS = Extensions.ALL_WITH_OPTIONALS

  override def render(source: String) = {
    val processor = new PegDownProcessor(EXTENSIONS, MAX_PARSING_TIME)
    val content = processor markdownToHtml source
    ContentWithAbstract(content, content)
  }

}