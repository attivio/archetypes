package ${package};

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;

import com.attivio.app.adminui.AbstractAdminViewServlet;
import com.attivio.model.error.ConfigurationError;

/**
 * Servlet that displays information from the ${artifactId} module.
 *
 * This code is provided as an example <i>only</i>.  The {@link AbstractAdminViewServlet} is a private and unsupported class and is
 * subject to change without notice.
 */
public class AdminServlet extends AbstractAdminViewServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected String getPanelTemplateRoot() {
    return "webapps/${artifactId}/WEB-INF/classes/templates";
  }

  /** {@inheritDoc} */
  @Override
  protected String getPageName() {
    return "${artifactId}";
  }

  @Override
  protected synchronized void populateVelocityContext(HttpServletRequest request, VelocityContext context) {
    try {
      super.populateVelocityContext(request, context);
      context.put("message", "Hello from module ${artifactId}");
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
