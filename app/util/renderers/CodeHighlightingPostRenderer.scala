package util.renderers

import org.jsoup.Jsoup
import scala.collection.convert.decorateAsScala._
import scala.collection.convert.wrapAsScala._
import util.Logging
import org.python.util.PythonInterpreter

object CodeHighlightingPostRenderer extends PostRenderer with Logging {

  override def include(renderFormat: String) = true

  override def render(content: ContentWithAbstract) =
    ContentWithAbstract(replaceCodes(content.abstractText), replaceCodes(content.content))

  def replaceCodes(content: String): String = {
    val doc = Jsoup parseBodyFragment content
    doc.body.select("code[class]").foreach {
      code =>
        val data = code.text
        val language = code.className
        log.info(s"Found code with format $language")
        val highlighted = Jsoup parseBodyFragment highlightWithPygments(data, language)
        if (highlighted.body.children.size == 1 && highlighted.body.child(0).children.size == 1) {
          // Pygments renders highlighted in inside <div class="highlight"><pre>, so its
          // code.highlight > div.highlight > pre after the conversion
          code.html(highlighted.body.child(0).child(0).html)
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

    val result = interpreter.get("result", classOf[String])

    println("---------------------------------")
    println(result)
    println("---------------------------------")
    result

  }

}