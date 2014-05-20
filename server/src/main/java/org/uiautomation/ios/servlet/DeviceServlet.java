/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.servlet;

import org.libimobiledevice.ios.driver.binding.model.ApplicationInfo;
import org.uiautomation.ios.RealDevice;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeviceServlet extends DriverBasedServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    /*StringBuilder b = new StringBuilder();
    b.append("<html>");
    b.append("<head>");
    b.append(
        "<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js'></script>");

    response.getWriter().write("Connected devices:</br>");

    response.getWriter().write(b.toString()); */
    response.getWriter().write(page());
    response.getWriter().close();
  }

  private String page() {
    StringBuilder b = new StringBuilder();

    b.append("<html lang=\"en\"> ");
    b.append("<head> ");
    b.append("<meta charset=\"utf-8\" />  ");
    b.append("<title>Connected devices</title>   ");
    b.append(
        " <link rel=\"stylesheet\" href=\"http://code.jquery.com/ui/1.10.1/themes/base/jquery-ui.css\" />");
    b.append("<script src=\"http://code.jquery.com/jquery-1.9.1.js\"></script>");
    b.append("<script src=\"http://code.jquery.com/ui/1.10.1/jquery-ui.js\"></script>");
    b.append("<script src=\"/static/devices.js\"></script>");
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
    for (RealDevice d : getDriver().getDeviceStore().getRealDevices()) {
      b.append("<li>");
      b.append(
          "<a href='#tabs-" + d.getUuid() + "'>" + d.getProductType() + " IOS:" + d.getIosVersion()
          + "(" + d.getName() + ")</a>");
      b.append("</li>");
    }
    b.append("</ul>");
    //TODO simulators.
    // content
    for (RealDevice d : getDriver().getDeviceStore().getRealDevices()) {
      b.append(device(d));
    }
    b.append("</div> ");
    b.append("</body> ");
    b.append("</html>  ");
    return b.toString();
  }

  private String device(RealDevice device) {
    StringBuilder b = new StringBuilder();

    b.append("<div id=\"tabs-" + device.getUuid() + "\">");
    b.append("<h3>Device info:</h3>");
    b.append("  <ul>");
    b.append("  <li>name: " + device.getName() + "</li>");
    b.append("  <li>id: " + device.getUuid() + "</li>");
    b.append("  <li>type: " + device.getType() + "</li>");
    b.append("  <li>build: " + device.getBuildVersion() + "</li>");
    b.append("  <li>product: " + device.getProductType() + "</li>");
    b.append("  <li>SDK: " + device.getIosVersion() + "</li>");
    b.append("  </ul>");

    b.append("<h3>Applications:</h3>");

    for (ApplicationInfo app : device.getApplications()) {
      b.append( app.getApplicationId() );
          //" <div class='archive' deviceId='" + device.getUuid() + "' app='"
          //+ app.getApplicationId() + "'>ARCHIVE</div>");
      b.append("<ul>");
      for (String key : app.keySet()) {
        b.append("<li>" + key + ": " + app.getProperty(key) + "</li>");
      }
      b.append("</ul>");
    }

    b.append("</div>");

    return b.toString();
  }
}
