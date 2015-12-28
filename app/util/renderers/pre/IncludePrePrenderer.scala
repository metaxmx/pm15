package util.renderers.pre

import util.renderers.MarkdownContentRenderer
import java.io.File
import org.apache.commons.io.FileUtils
import com.google.common.base.Charsets.UTF_8
import util.Logging
import util.renderers.RenderContext

object IncludePrePrenderer extends PreRenderer with Logging {

  val include_regex = """(?i)@include\(([a-zA-Z0-9_.-]+)\)""".r

  override def include(implicit context: RenderContext): Boolean = context.format match {
    case MarkdownContentRenderer.renderFormat => true
    case _                                    => false
  }

  override def render(content: String)(implicit context: RenderContext): String = {
    include_regex.replaceAllIn(content, m => {
      val filename = m group 1
      val includefile = context.attachmentFolder flatMap { dir => Option(new File(dir, filename)) filter (_.exists) filter (_.getParentFile == dir) }
      includefile.fold {
        s"""Error: Includefile "$filename" not found"""
      } {
        file =>
          val content = try {
            FileUtils.readFileToString(file, UTF_8)
          } catch {
            case e: Exception =>
              log.error(s"Error reading includefile $filename", e)
              s"""Error reading Includefile "$filename""""
          }
          content
      }
    })
  }

}