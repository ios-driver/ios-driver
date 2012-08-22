package org.uiautomation.ios.ide.views;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.server.utils.ClassicCommands;


public class HomeView implements View {

  @Override
  public void render(HttpServletResponse response) throws Exception {
    StringBuilder b = new StringBuilder();
    b.append("<html>");
    b.append("<head>");
    b.append(" <link rel='stylesheet' href='" + getResource("ide.css") + "'  type='text/css'/>");
    b.append("<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js'></script>");
    b.append("<script type='text/javascript' src='" + getResource("begin.js") + "'></script>");

    b.append("</head>");
    b.append("<body>");
   b.append("<form action='start' method='GET'>");

    b.append("<input  type='text' name='" + IOSCapabilities.DEVICE + "' value= '"
        + IOSDevice.iPhoneSimulator + "' />");


    //b.append(select(IOSCapabilities.LOCALE, getLocales()));
    //b.append(select(IOSCapabilities.LANGUAGE, getLanguage()));

    b.append(select(IOSCapabilities.SDK_VERSION, ClassicCommands.getInstalledSDKs()));
    //b.append(select(IOSCapabilities.AUT, supportedApps));
    //b.append(select(IOSCapabilities.TIME_HACK, new String[] {"false", "true"}));

    b.append("<br><input type='submit' value='start'>");
    b.append("</form>");
    b.append("</body>");
    b.append("</html>");



    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);

    response.getWriter().print(b.toString());
  }
  
  private String select(String name, List<String> options) {
    StringBuilder b = new StringBuilder();
    b.append("<br>" + name + "<select name='" + name + "', id = '" + name + "'>");
    boolean first = true;
    for (String option : options) {
      if(IOSCapabilities.AUT.equals(name)){
        String[] tmp = option.split("/");
        if(first){
          b.append("<br><option value= '" + option + "', selected = '" + option + "'>" + tmp[tmp.length-1] + "</option>");
          first = false;
        }
        else{
          b.append("<br><option value= '" + option + "'>" + tmp[tmp.length-1] + "</option>");
        }
      }
      else{
        b.append("<br><option>" + option + "</option>");
      }
    }
    b.append("<br></select>");
    return b.toString();

  }

  private String getResource(String name) {
    String res = "resources/" + name;
    return res;
  }

}
