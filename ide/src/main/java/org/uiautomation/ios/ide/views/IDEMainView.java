package org.uiautomation.ios.ide.views;

import javax.servlet.http.HttpServletResponse;

import org.uiautomation.ios.UIAModels.Orientation;
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
      b.append("<html>");
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

      b.append("<div id ='details' class='details" + suffix + "'>details</div>");



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



      b.append("<div id ='tree'  ></div>");
      b.append("<div id ='details' >details</div>");


      String d = "iphone";
      if (model.getCapabilities().getDevice() == IOSDevice.iPadSimulator) {
        d = "ipad";
      }
      b.append("<script >configure('" + d + "','" + model.getDeviceOrientation() + "');</script>");
      b.append("<script >resize();</script>");
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
}
