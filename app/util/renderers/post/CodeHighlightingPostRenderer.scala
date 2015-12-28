package util.renderers.post

import org.jsoup.Jsoup
import scala.collection.convert.wrapAsScala._
import org.python.util.PythonInterpreter
import util.renderers.ContentWithAbstract
import util.renderers.RenderContext
import org.jsoup.nodes.Node

object CodeHighlightingPostRenderer extends PostRenderer {

  override def include(implicit context: RenderContext) = true

  override def render(content: ContentWithAbstract)(implicit context: RenderContext) = content map replaceCodes

  def replaceCodes(content: String): String = {
    val doc = Jsoup parseBodyFragment content
    doc.body.select("code[class]").foreach {
      code =>
        val data = code.text
        val language = code.className
        val highlighted = Jsoup parseBodyFragment highlightWithPygments(data, language)
        if (highlighted.body.children.size == 1 && highlighted.body.child(0).children.size == 1) {
          // Pygments renders highlighted in inside <div class="highlight"><pre>, so its
          // code.highlight > div.highlight > pre after the conversion
          val highlightedContainer = highlighted.body.child(0).child(0)
          code.empty()
          code.appendChild(highlightedContainer)
          code.addClass("highlight")
        }
      // else: The syntax highlighter for the language was not found, no replacement
    }
    doc.body.html
  }

  def highlightWithPygments(code: String, language: String): String = {
    val interpreter = new PythonInterpreter();
    interpreter.set("code", code);
    interpreter.set("language", language);

    // Simple use Pygments as you would in Python
    interpreter exec """|from pygments import highlight
                        |from pygments.lexers import ScalaLexer
                        |from pygments.lexers import get_lexer_by_name
                        |from pygments.formatters import HtmlFormatter
                        |result = code
                        |try:
                        |    lexer = get_lexer_by_name(language)
                        |    result = highlight(code, lexer, HtmlFormatter())
                        |except:
                        |    result = code""".stripMargin

    interpreter.get("result", classOf[String])
  }

}