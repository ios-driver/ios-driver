/*
 * Copyright 2012 ios-driver committers.
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
package org.uiautomation.ios.inspector.views;

import freemarker.template.Configuration;
import freemarker.template.Template;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.inspector.model.IDESessionModel;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

public class IDEMainView implements View {

  private final IDESessionModel model;
  private final String servletPath;

  public IDEMainView(IDESessionModel model, String servletPath) {
    this.model = model;
    this.servletPath = servletPath;
  }


  @Override
  public void render(HttpServletResponse response) throws Exception {
    Configuration conf = new Configuration();
    conf.setClassForTemplateLoading(this.getClass(), "/");
    Template template = conf.getTemplate("/inspector/inspector.html");
    StringWriter writer = new StringWriter();
    Map<String, Object> map = new HashMap<String, Object>();

    List<String> cssList = new ArrayList<String>();
    cssList.add(getResource("inspector/inspector.css"));
    cssList.add(getResource("ide.css"));
    cssList.add(getResource("jquery.layout.css"));

    map.put("cssList", cssList);

    List<String> jsList = new ArrayList<String>();
    jsList.add(getResource("jquery-ui-1.10.2.custom/js/jquery-ui-1.10.2.custom.min.js"));
    jsList.add(getResource("inspector/inspector.js"));
    jsList.add(getResource("jquery.jstree.js"));
    jsList.add(getResource("jquery.xpath.js"));
    jsList.add(getResource("prettify.js"));
    jsList.add(getResource("Logger.js"));
    jsList.add(getResource("Recorder.js"));
    jsList.add(getResource("inspector.js"));
    jsList.add(getResource("ide.js"));
    jsList.add(getResource("uiactions.js"));
    jsList.add(getResource("jquery.layout1.3.js"));

    map.put("jsList", jsList);

    DeviceType device = model.getCapabilities().getDevice();
    DeviceVariation variation = model.getCapabilities().getDeviceVariation();
    Orientation orientation = model.getDeviceOrientation();

    map.put("frame", getFrame(device, variation, orientation));


    map.put("screenshot", getScreen(device));

    String type = "iphone";
    if (model.getCapabilities().getDevice() == DeviceType.ipad) {
      type = "ipad";
    }

    map.put("type", type);
    map.put("variation", variation);
    map.put("orientation", orientation);

    template.process(map, writer);
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);

    response.getWriter().print(writer.toString());

  }


  public void render2(HttpServletResponse response) throws Exception {
    try {
      StringBuilder b = new StringBuilder();
      b.append("<html>");
      b.append("<head>");
      //github stuff.
      //b.append(" <link rel='stylesheet' href='" + getResource("stylesheet.css") + "'  type='text/css'/>");
      b.append(" <link rel='stylesheet' href='" + getResource("ide.css") + "'  type='text/css'/>");
      b.append(
          " <link rel='stylesheet' href='" + getResource("prettify.css") + "'  type='text/css'/>");
      b.append(
          "<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js'></script>");
      b.append(
          " <link rel=\"stylesheet\" href=\"http://code.jquery.com/ui/1.10.1/themes/base/jquery-ui.css\" />");
      b.append("<script src=\"http://code.jquery.com/ui/1.10.1/jquery-ui.js\"></script>");
      b.append(
          "<script type='text/javascript' src='" + getResource("jquery.jstree.js") + "'></script>");
      b.append(
          "<script type='text/javascript' src='" + getResource("jquery.xpath.js") + "'></script>");
      b.append("<script type='text/javascript' src='" + getResource("prettify.js") + "'></script>");

      DeviceType device = model.getCapabilities().getDevice();
      DeviceVariation variation = model.getCapabilities().getDeviceVariation();

      b.append(
          "<script type='text/javascript' src='" + getResource("Logger.js") + "'></script>");
      b.append(
          "<script type='text/javascript' src='" + getResource("Recorder.js") + "'></script>");
      b.append(
          "<script type='text/javascript' src='" + getResource("inspector.js") + "'></script>");

      b.append("<script type='text/javascript' src='" + getResource("ide.js") + "'></script>");
      b.append(
          "<script type='text/javascript' src='" + getResource("uiactions.js") + "'></script>");

      b.append("<script>");
      b.append(" $(function() {");
      b.append(" $( \"#tabs\" ).tabs();");
      b.append("});");
      b.append("</script>");

      b.append("</head>");

      b.append("<body onload=\"prettyPrint()\">");
      b.append("<html>");
      b.append("<div id='greyout'></div>");
      b.append("<div id='simulator'>");

      b.append("<div id='mouseOver'></div>");
      b.append(" <div id='rotationCenter'>");

      b.append("<div id='frame'>");
      b.append("<img src='" + getFrame(device, variation, Orientation.LANDSCAPE) + "' />");
      b.append("        <div id='screen'>");

      int width = 320;
      if (device == DeviceType.ipad) {
        width = 768;
      }
      b.append("         <img id='screenshot' src='" + getScreen(device) + "' width='" + width
               + "px' />");
      b.append("</div>");
      b.append("</div>");
      b.append("</div>");
      b.append("</div>");

      b.append(
          "<div id='xpathHelper' >Xpath Expression:</br><input type='text' value='' id='xpathInput' /><div id='xpathLog' > log</div></div>");
      b.append("<div id ='detailsparent' >");

      b.append("<div id ='details' ></div>");
      b.append("</div>");

      b.append("<div id ='tree'  ></div>");

      String d = "iphone";
      if (model.getCapabilities().getDevice() == DeviceType.ipad) {
        d = "ipad";
      }
      b.append("<script >configure('" + d + "','" + variation + "','" + model.getDeviceOrientation()
               + "');</script>");
      b.append("<script >resize();</script>");
      b.append("<div id ='topmenu'>");
      b.append("<div id=\"picture\"/>");
      String icon = getIcon();
      if (icon != null) {
        b.append("<img src=\"" + getIcon() + "\"/>");
      }

      b.append("</div>");
      b.append("<ul>");
      b.append("<li id=\"capabilities\"><a href=\"#\">See Capabilities</a></li>");
      b.append("<li id=\"htmlshow\"><a href=\"#\">See HTML</a></li>");
      b.append("</ul>");
      b.append("</div>");

      b.append(getScriptTabs());

      /*
       * OVERLAY Capabilities
       */
      b.append("<div class=\"overlay\" id=\"overlay\" style=\"display:none;\"></div>");
      b.append("<div class=\"box\" id=\"box\">");
      b.append("<a class=\"boxclose\" id=\"boxclose\"><p class=\"arrow-left\"></p></a>");
      b.append("<h4>Capabilities</h4>");
      b.append("<p>");
      b.append(displayCapabilities());
      b.append("</p>");
      b.append("<h4>Supported Languages</h4>");
      b.append("<p>");
      b.append(getListOfLanguagesInHTML());
      b.append("</p>");
      b.append("</div>");
      /* END OVERLAY CAPABILITIES */

      /*
       * OVERLAY HTML
       */
      b.append("<div class=\"overlayhtml\" id=\"overlayhtml\" style=\"display:none;\"></div>");
      b.append("<div class=\"boxhtml\" id=\"boxhtml\">");
      b.append(
          "<a class=\"boxclosehtml\" id=\"boxclosehtml\"><p class=\"arrow-right-html\"></p></a>");
      b.append("<h4>Web Inspector</h4>");
      b.append("<iframe id=\"webinspector\" src=\"ide/session/" + model.getSession()
               + "/latestWebView\"></iframe> ");
      b.append("</div>");
      /* END OVERLAY HTML */

      b.append("</body>");
      b.append("</html>");

      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      response.setStatus(200);

      response.getWriter().print(b.toString());
    } catch (Exception e) {
      throw new WebDriverException(e);
    }

  }

  private String displayCapabilities() {
    if (model != null) {
      StringBuffer capabilities = new StringBuffer();
      Set<String> keys = model.getCapabilities().getRawCapabilities().keySet();
      for (String capability : keys) {
        capabilities.append("<p><b>" + capability + "</b>: "
                            + model.getCapabilities().getRawCapabilities().get(capability)
                            + "</p>");
      }
      return capabilities.toString();
    }
    return "No capabilities available. Model = null";
  }

  private String getScreen(DeviceType device) {
    return getResource("session/" + model.getSession().getSessionId() + "/screenshot.png");
  }

  private String getFrame(DeviceType device, DeviceVariation variation, Orientation o) {

    if (device == DeviceType.iphone) {
      if (variation == DeviceVariation.Retina4) {
        return getResource("images/frames/frame_iphone5_" + o.instrumentsValue() + ".png");
      } else {
        return getResource("images/frames/frame_iphone_" + o.instrumentsValue() + ".png");
      }
    } else {
      return getResource("images/frames/frame_ipad_" + o.instrumentsValue() + ".jpg");
    }
  }

  private String getResource(String name) {
    String res = servletPath + "/resources/" + name;
    return res;
  }

  private String getListOfLanguagesInHTML() throws Exception {
    JSONObject jsonApp = getAppFromStatus();
    JSONArray supportedLanguages = (JSONArray) jsonApp.get("supportedLanguages");

    StringBuffer buffer = new StringBuffer();
    buffer.append("<ul>");
    for (int i = 0; i < supportedLanguages.length(); i++) {
      buffer.append("<li>" + supportedLanguages.getString(i) + "</li>");
    }
    buffer.append("<ul>");

    return buffer.toString();
  }

  private String getIcon() throws Exception {

    JSONObject app = getAppFromStatus();
    JSONObject resources = app.optJSONObject("resources");
    if (resources != null) {
      String icon = resources.getString("CFBundleIconFile");

      String h = model.getEndPoint().getHost();
      int p = model.getEndPoint().getPort();
      return "http://" + h + ":" + p + icon;
    }
    return null;

  }

  private JSONObject getStatus() throws Exception {
    return model.getStatus();
  }

  private JSONObject getAppFromStatus() throws Exception {
    JSONObject status = getStatus();
    JSONArray array = status.getJSONObject("value").getJSONArray("supportedApps");
    for (int i = 0; i < array.length(); i++) {
      JSONObject jsonApp = array.getJSONObject(i);
      String other = (String) jsonApp.get("CFBundleIdentifier");
      String me = (String) model.getCapabilities().getRawCapabilities().get("CFBundleIdentifier");
      if (other.equals(me)) {
        return jsonApp;
      }
    }
    return null;
  }

  public String getScriptTabs() {
    StringBuilder b = new StringBuilder();
    b.append("<div id=\"tabs\">");
    b.append("<ul>");
    b.append("<li><a href=\"#tabs-java\">Java</a></li>");
    b.append("<li><a href=\"#tabs-raw\">Raw</a></li>");
    b.append("<li><a href=\"#tabs-logs\">Logs</a></li>");
    b.append("</ul>");

    b.append("<div id=\"tabs-java\" class='tab' >");
    b.append("<pre id='java' class=\"prettyprint\"></pre>");

    b.append("</div>");

    b.append("<div id=\"tabs-raw\" class='tab'>");
    b.append("TAB2");
    b.append("</div>");

    b.append("<div id=\"tabs-logs\" class='tab' >");
    b.append("TAB3");
    b.append("</div>");

    return b.toString();
  }
}
