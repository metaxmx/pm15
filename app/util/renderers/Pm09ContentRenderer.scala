package util.renderers

/**
 * Content Renderer for the "Planet Metax '09" Content Format.
 */
object Pm09ContentRenderer extends ContentRenderer {

  override val renderFormat = "pm09"

  override def render(source: String) = ContentWithAbstract(source, source)

}