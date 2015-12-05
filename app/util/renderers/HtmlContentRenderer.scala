package util.renderers

object HtmlContentRenderer extends ContentRenderer {

  override val renderFormat = "html"

  override def render(source: String)(implicit context: RenderContext) = ContentWithAbstract(source, source)

}