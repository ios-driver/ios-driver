package org.uiautomation.ios.ide.views;

import javax.servlet.http.HttpServletResponse;

import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.ide.model.IDESessionModel;

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
      
      if (model.getCapabilities().getDevice() == IOSDevice.iPhoneSimulator) {
        b.append("<script > var offsetX = 49; var offsetY = 143;</script>");
      }else{
        b.append("<script > var offsetX = 68; var offsetY = 68;</script>");      
      }
    
                    
     
                  
      
      b.append("<script type='text/javascript' src='" + getResource("ide.js") + "'></script>");

     

      b.append("</head>");


      b.append("<body>");
      b.append("<html>");
      b.append("<div id ='highlight' ></div>");

      String suffix = "iPad";
      if (model.getCapabilities().getDevice() == IOSDevice.iPhoneSimulator) {
        suffix = "iPhone";
      }

      b.append("<div id='frame' ><img src='" + getResource("frame" + suffix + ".png")
          + " '/></div>");
      b.append("<div id='mouseOver' class='mouseOver" + suffix + "' ></div>");
      b.append("<div id='screen" + suffix + "' ><img src='"
          + getResource("session/" + model.getSession().getSessionId() + "/screenshot.png")
          + "' /></div>");



      b.append("<div id ='tree' class='tree" + suffix + "' ></div>");

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



  private String getResource(String name) {
    String res = servletPath + "/resources/" + name;
    return res;
  }
}
