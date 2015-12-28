package util.renderers.pre

import util.renderers.MarkdownContentRenderer
import java.io.File
import org.apache.commons.io.FileUtils
import com.google.common.base.Charsets.UTF_8
import util.Logging
import util.renderers.RenderContext
import scala.collection.convert.WrapAsScala._
import scala.util.matching.Regex

object IncludePrePrenderer extends PreRenderer with Logging {

  val include_regex = """(?i)@include\(([a-zA-Z0-9_.-]+)\)""".r

  val include_segment_regex = """(?i)@include\(([a-zA-Z0-9_.-]+)@([0-9]+)-([0-9]+)\)""".r

  override def include(implicit context: RenderContext): Boolean = context.format match {
    case MarkdownContentRenderer.renderFormat => true
    case _                                    => false
  }

  override def render(content: String)(implicit context: RenderContext): String = {
    val replaced1 = include_regex.replaceAllIn(content, m => {
      val filename = m group 1
      val includefile = context.attachmentFolder flatMap { dir => Option(new File(dir, filename)) filter (_.exists) filter (_.getParentFile == dir) }
      includefile.fold {
        s"""Error: Includefile "$filename" not found"""
      } {
        file =>
          val includeContent = try {
            FileUtils.readFileToString(file, UTF_8)
          } catch {
            case e: Exception =>
              log.error(s"Error reading includefile $filename", e)
              s"""Error reading Includefile "$filename""""
          }
          Regex.quoteReplacement(includeContent)
      }
    })
    val replaced2 = include_segment_regex.replaceAllIn(replaced1, m => {
      val filename = m group 1
      val begin = (m group 2).toInt
      val end = (m group 3).toInt
      val includefile = context.attachmentFolder flatMap { dir => Option(new File(dir, filename)) filter (_.exists) filter (_.getParentFile == dir) }
      includefile.fold {
        s"""Error: Includefile "$filename" not found"""
      } {
        file =>
          val includeContent = try {
            FileUtils.readLines(file, UTF_8).toSeq.drop(begin - 1).take(1 + end - begin).mkString("\n")
          } catch {
            case e: Exception =>
              log.error(s"Error reading includefile $filename", e)
              s"""Error reading Includefile "$filename""""
          }
          Regex.quoteReplacement(includeContent)
      }
    })
    replaced2
  }

}