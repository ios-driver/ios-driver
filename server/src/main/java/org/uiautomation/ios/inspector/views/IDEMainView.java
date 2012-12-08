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

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.inspector.model.IDESessionModel;

public class IDEMainView implements View {

  private final IDESessionModel model;
  private final String servletPath;

  public IDEMainView(IDESessionModel model, String servletPath) {
    this.model = model;
    this.servletPath = servletPath;
  }

  @Override
  public void render(HttpServletResponse response) throws Exception {
    try {
      StringBuilder b = new StringBuilder();
      b.append("<html>");
      b.append("<head>");

      b.append(" <link rel='stylesheet' href='" + getResource("ide.css") + "'  type='text/css'/>");
      b.append("<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js'></script>");
      b.append("<script type='text/javascript' src='" + getResource("jquery.jstree.js") + "'></script>");

      Device device = model.getCapabilities().getDevice();
      DeviceVariation variation=model.getCapabilities().getDeviceVariation();

      b.append("<script type='text/javascript' src='" + getResource("ide.js") + "'></script>");
      b.append("<script type='text/javascript' src='" + getResource("uiactions.js") + "'></script>");

      b.append("</head>");

      b.append("<body>");
      b.append("<html>");

      b.append("<div id='simulator'>");

      b.append("<div id ='highlight' ></div>");
      b.append("<div id='mouseOver'></div>");
      b.append(" <div id='rotationCenter'>");

      b.append("<div id='frame'>");
      b.append("<img src='" + getFrame(device,variation) + "' />");
      b.append("        <div id='screen'>");
      
      int width = 320;
      if (device==Device.ipad){
        width=768;
      }
      b.append("         <img src='" + getScreen(device) + "' width='"+width+"px' />");
      b.append("</div>");
      b.append("</div>");
      b.append("</div>");
      b.append("</div>");

      b.append("<div id ='detailsparent' >");
      b.append("<div id ='htmlmenu'>");
      b.append("<ul>");
      b.append("<li id=\"htmlshow\"><a href=\"#\">See HTML</a></li>");
      b.append("</ul>");
      b.append("</div>");
      b.append("<div id ='details' ></div>");
      b.append("</div>");
      
      
      b.append("<div id ='tree'  ></div>");

      String d = "iphone";
      if (model.getCapabilities().getDevice() == Device.ipad) {
        d = "ipad";
      }
      b.append("<script >configure('" + d + "','"+ variation + "','" + model.getDeviceOrientation() + "');</script>");
      b.append("<script >resize();</script>");
      b.append("<div id ='topmenu'>");
      b.append("<div id=\"picture\"/>");
      String icon = getIcon();
      if (icon!=null){
        b.append("<img src=\"" + getIcon() + "\"/>");
      }
          
     
      b.append("</div>");
      b.append("<ul>");
      b.append("<li id=\"capabilities\"><a href=\"#\">See Capabilities</a></li>");
      b.append("</ul>");
      b.append("</div>");
      
      

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
      b.append("<a class=\"boxclosehtml\" id=\"boxclosehtml\"><p class=\"arrow-right-html\"></p></a>");
      b.append("<h4>Web Inspector</h4>");
      b.append("<iframe id=\"webinspector\" src=\"ide/session/"+model.getSession()+"/latestWebView\"></iframe> ");
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
            + model.getCapabilities().getRawCapabilities().get(capability) + "</p>");
      }
      return capabilities.toString();
    }
    return "No capabilities available. Model = null";
  }

  private String getScreen(Device device) {
    return getResource("session/" + model.getSession().getSessionId() + "/screenshot.png");
  }

  private String getFrame(Device device,DeviceVariation variation) {
    
    
    if (device == Device.iphone ) {
      if (variation == DeviceVariation.Retina4){
        return getResource("frame_iphone5.png");
      }else{
        return getResource("frame_iphone.png");
      }
    } else {
      return getResource("frame_ipad.png");
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
  
}
