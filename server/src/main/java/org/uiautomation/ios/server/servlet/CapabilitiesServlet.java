package org.uiautomation.ios.server.servlet;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.BeanToJsonConverter;
import org.uiautomation.ios.IOSCapabilities;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CapabilitiesServlet extends DriverBasedServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    response.getWriter().write(page());
    response.getWriter().close();
  }


  private String page() {
    StringBuilder b = new StringBuilder();
    List<IOSCapabilities> caps = getDriver().getAllCapabilities();
    int i = 0;
    for (IOSCapabilities cap : caps) {
      String title = cap.getBundleId() + " on " + cap.getDevice();

      if (cap.isSimulator()) {
        title += "(Simulator)";
      }

      b.append("<h3>" + title + "</h3>");
      try {
        String json = new BeanToJsonConverter().convert(cap);
        JSONObject o = new JSONObject(json);
        b.append(o.toString(2));
      } catch (JSONException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      i++;
    }
    return b.toString();
  }

}
