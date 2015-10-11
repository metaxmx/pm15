package util.renderers

import org.pegdown.PegDownProcessor

object MarkdownContentRenderer extends ContentRenderer {

  override val renderFormat = "md"

  override def render(source: String) = {
    val processor = new PegDownProcessor()
    val content = processor markdownToHtml source
    ContentWithAbstract(content, content)
  }

}