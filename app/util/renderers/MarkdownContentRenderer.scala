package util.renderers

import org.pegdown.PegDownProcessor
import org.pegdown.Extensions

object MarkdownContentRenderer extends ContentRenderer {

  override val renderFormat = "md"

  def MAX_PARSING_TIME = 20000

  def EXTENSIONS = Extensions.ALL_WITH_OPTIONALS ^ Extensions.ANCHORLINKS

  override def render(source: String)(implicit context: RenderContext) = {
    val processor = new PegDownProcessor(EXTENSIONS, MAX_PARSING_TIME)
    val (abstrSource, contentSource) = splitAbstract(source)
    val abstr = processor markdownToHtml abstrSource
    val content = processor markdownToHtml contentSource
    ContentWithAbstract(abstr, content)
  }

  def splitAbstract(source: String): (String, String) =
    split2(source, "--abstract--") orElse
      split2(source, "---abstract---") orElse
      split2(source, "~~~") getOrElse
      (source, source)

  def split2(source: String, exp: String): Option[(String, String)] = {
    val split = source split exp
    if (split.length == 2) Some((split(0), split(0) + split(1))) else None
  }

}