package org.uiautomation.ios.server.servlet;

import org.json.JSONException;
import org.uiautomation.ios.server.application.APPIOSApplication;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApplicationsServlet extends DriverBasedServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    try {
      response.getWriter().write(page());
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    response.getWriter().close();
  }


  private String page() throws JSONException {
    StringBuilder b = new StringBuilder();

    b.append("<html lang=\"en\"> ");
    b.append("<head> ");
    b.append("<meta charset=\"utf-8\" />  ");
    b.append("<title>Connected devices</title>   ");
    b.append(
        " <link rel=\"stylesheet\" href=\"http://code.jquery.com/ui/1.10.1/themes/base/jquery-ui.css\" />");
    b.append("<script src=\"http://code.jquery.com/jquery-1.9.1.js\"></script>");
    b.append("<script src=\"http://code.jquery.com/ui/1.10.1/jquery-ui.js\"></script>");
    b.append("<script>");
    b.append(" $(function() {");
    b.append("  $( \"#tabs\" ).tabs();");
    b.append("});");
    b.append("</script>");
    b.append("</head>");
    b.append("<body>");

    b.append("<div id=\"tabs\"> ");
    // tabs
    b.append("<ul>");
    b.append("<li><a href='#tabs-app'>Simulator apps</a></li>");
    b.append("<li><a href='#tabs-ipa'>Real device apps</a></li>");
    b.append("</ul>");

    // content
    b.append(content(true));
    b.append(content(false));

    b.append("</div> ");
    b.append("</body> ");
    b.append("</html>  ");
    return b.toString();
  }

  private String content(boolean real) throws JSONException {
    StringBuilder b = new StringBuilder();
    String id;
    List<APPIOSApplication> list;
    if (real) {
      id = "ipa";
      list = getDriver().getApplicationStore().getRealDeviceApplications();
    } else {
      id = "app";
      list = getDriver().getApplicationStore().getSimulatorApplications();
    }
    b.append("<div id=\"tabs-" + id + "\">");

    for (APPIOSApplication app : list) {
      b.append("<h3>" + app.getBundleId() + "</h3>");
      b.append("  <pre>");
      b.append(app.getMetadata().toString(2));
      b.append("  </pre>");
    }

    b.append("</div>");

    return b.toString();
  }


}
