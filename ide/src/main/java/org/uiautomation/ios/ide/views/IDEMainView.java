package org.uiautomation.ios.ide.views;

import java.net.URL;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.ide.model.IDESessionModel;
import org.uiautomation.ios.ide.views.positioning.IPadDevicePositioning;

public class IDEMainView implements View {

  private final IDESessionModel model;
  private final String servletPath;

  public IDEMainView(IDESessionModel model, String servletPath) {
    this.model = model;
    this.servletPath = servletPath;
  }



  public void render2(HttpServletResponse response) throws Exception {
    try {
      StringBuilder b = new StringBuilder();
      b.append("<html>");
      b.append("<head>");

      b.append(" <link rel='stylesheet' href='" + getResource("ide.css") + "'  type='text/css'/>");
      b.append("<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js'></script>");
      b.append("<script type='text/javascript' src='" + getResource("jquery.jstree.js")
          + "'></script>");

      IPadDevicePositioning position =
          new IPadDevicePositioning(model.getDeviceOrientation(), 25, 25);
      int degree = model.getDeviceOrientation().getRotationInDegree();

      /*
       * if (model.getCapabilities().getDevice() == IOSDevice.iPhoneSimulator) {
       * b.append("<script > var offsetX = 49; var offsetY = 143;</script>"); } else {
       * b.append("<script > var offsetY = " + position.getScreenTop() + "; var offsetX = " +
       * position.getScreenLeft() + ";</script>"); b.append("<script > var realOffsetY = " +
       * position.getRealScreenTop() + "; var realOffsetX = " + position.getRealScreenLeft() +
       * ";</script>"); }
       */



      b.append("<script type='text/javascript' src='" + getResource("ide.js") + "'></script>");



      b.append("</head>");


      b.append("<body>");
      // Menu div
      b.append("<div id=\"menu\"/></div>");
      b.append("<div id ='highlight' ></div>");

      String suffix = "iPad";
      if (model.getCapabilities().getDevice() == IOSDevice.iPhoneSimulator) {
        suffix = "iPhone";
      }


      String rotate = "-moz-transform:rotate(" + degree + "deg);";
      b.append("<div id='frame' ");
      b.append("style='" + rotate + "top:" + position.getFrameTop() + "px;" + "left : "
          + position.getFrameLeft() + "px' >" +

          "<img src='" + getResource("frame" + suffix + ".png") + " '/></div>");

      b.append("<div id='mouseOver' " + "style='left:" + position.getRealScreenLeft() + "px;"
          + "top:" + position.getRealScreenTop() + "px;" + "width:" + position.getRealScreenWidth()
          + "px;" + "height :" + position.getRealScreenHeight() + "px ' ></div>");
      b.append("<div id='screen' " + "style='" + rotate + "top:" + position.getScreenTop() + "px;"
          + "left:" + position.getScreenLeft() + "px' >" + "<img src='"
          + getResource("session/" + model.getSession().getSessionId() + "/screenshot.png")
          + "' /></div>");



      b.append("<div id ='tree'  ></div>");

      b.append("<div id ='details' class='details" + suffix + "'></div>");



      b.append("<div id ='actions' class='actions" + suffix + "'>actions");
      b.append("<form action='tap' method='GET'>");

      b.append("<div id ='reference'></div>");

      b.append(" <input type='submit' value='tap'>");
      b.append("</form>");

      b.append("<form action='debug' method='GET'>");

      b.append("X: <input type='text' name='x' >");
      b.append("Y: <input type='text' name='y' >");

      b.append("<input type='submit' value='debugTap'>");
      b.append("</form>");
      b.append("</div>");


      b.append("</body>");
      b.append("</html>");



      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      response.setStatus(200);

      response.getWriter().print(b.toString());
    } catch (Exception e) {
      throw new IOSAutomationException(e);
    }

  }

  @Override
  public void render(HttpServletResponse response) throws Exception {
    try {
      StringBuilder b = new StringBuilder();
      b.append("<html>");
      b.append("<head>");

      b.append(" <link rel='stylesheet' href='" + getResource("ide.css") + "'  type='text/css'/>");
      b.append("<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js'></script>");
      b.append("<script type='text/javascript' src='" + getResource("jquery.jstree.js")
          + "'></script>");


      /*
       * IPadDevicePositioning position = new IPadDevicePositioning(model.getDeviceOrientation(),
       * 25, 25); int degree = model.getDeviceOrientation().getRotationInDegree();
       */
      IOSDevice device = model.getCapabilities().getDevice();

      /*
       * if (model.getCapabilities().getDevice() == IOSDevice.iPhoneSimulator) {
       * b.append("<script > var offsetX = 49; var offsetY = 143;</script>"); } else {
       * b.append("<script > var offsetY = " + position.getScreenTop() + "; var offsetX = " +
       * position.getScreenLeft() + ";</script>"); b.append("<script > var realOffsetY = " +
       * position.getRealScreenTop() + "; var realOffsetX = " + position.getRealScreenLeft() +
       * ";</script>"); }
       */



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
      b.append("<img src='" + getFrame(device) + "' />");
      b.append("        <div id='screen'>");
      b.append("         <img src='" + getScreen(device) + "' />");
      b.append("</div>");
      b.append("</div>");
      b.append("</div>");
      b.append("</div>");


      b.append("<div id ='details' ></div>");
      b.append("<div id ='tree'  ></div>");



      String d = "iphone";
      if (model.getCapabilities().getDevice() == IOSDevice.iPadSimulator) {
        d = "ipad";
      }
      b.append("<script >configure('" + d + "','" + model.getDeviceOrientation() + "');</script>");
      b.append("<script >resize();</script>");

      b.append("<div id ='footer'>");
      b.append("<div id=\"picture\"/>");
      b.append("<img src=\"" + getIcon() + "\"/>");
      b.append("</div>");
      b.append("<ul>");
      b.append("<li id=\"capabilities\"><a href=\"#\">See Capabilities</a></li>");
      b.append("<li id=\"languages\"><a href=\"#\">Suported Languages</a></li>");
      b.append("<li><a href=\"#\">Debug Options</a></li>");
      b.append("</ul>");
      b.append("</div>");

      /*
       * OVERLAY Capabilities
       */
      b.append("<div class=\"overlay\" id=\"overlay\" style=\"display:none;\"></div>");

      b.append("<div class=\"box\" id=\"box\">");
      b.append("<a class=\"boxclose\" id=\"boxclose\"></a>");
      b.append("<h3>Capabilities</h3>");
      b.append("<p>");
      b.append(displayCapabilities());
      b.append("</p>");
      b.append("</div>");
      /* END OVERLAY CAPABILITIES */

      /* OVERLAY LANGUAGES */
      b.append("<div class=\"overlaylanguages\" id=\"overlayLanguages\" style=\"display:none;\"></div>");

      b.append("<div class=\"boxlanguages\" id=\"boxlanguages\">");
      b.append("<a class=\"boxcloselanguages\" id=\"boxcloselanguages\"></a>");
      b.append("<h3>Supported Languages</h3>");
      b.append("<p>");
      b.append(getListOfLanguagesInHTML());
      b.append("</p>");
      b.append("</div>");

      /* END OVERLAY FOR LANGUAGES */

      b.append("</body>");
      b.append("</html>");



      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      response.setStatus(200);

      response.getWriter().print(b.toString());
    } catch (Exception e) {
      throw new IOSAutomationException(e);
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



  private String getScreen(IOSDevice device) {
    return getResource("session/" + model.getSession().getSessionId() + "/screenshot.png");
  }



  private String getFrame(IOSDevice device) {
    if (device == IOSDevice.iPhoneSimulator) {
      return getResource("frameiphone.png");
    } else {
      return getResource("frameipad.png");
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
    JSONObject resources = app.getJSONObject("resources");
    String icon = resources.getString("CFBundleIconFile");
    // TODO: URL HARDCODED
    return "http://localhost:4444" + icon;

  }


  private JSONObject getStatus() throws Exception {
    HttpClient client = HttpClientFactory.getClient();
    // TODO: URLHARDCODED
    String url = "http://localhost:4444/wd/hub" + "/status";
    URL u = new URL(url);
    BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", url);

    HttpHost h = new HttpHost(u.getHost(), u.getPort());
    HttpResponse response = client.execute(h, r);

    JSONObject o = Helper.extractObject(response);

    return o;
  }



  private JSONObject getAppFromStatus() throws Exception {
    JSONObject status = getStatus();
    JSONArray array = status.getJSONObject("value").getJSONArray("supportedApps");
    for (int i = 0; i < array.length(); i++) {
      JSONObject jsonApp = array.getJSONObject(i);
      if (jsonApp.get("CFBundleIdentifier").equals(
          model.getCapabilities().getRawCapabilities().get("bundleid"))) {
        return jsonApp;
      }
    }
    return null;
  }
}
