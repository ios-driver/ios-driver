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
package org.uiautomation.ios.inspector.controllers;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.inspector.IDEServlet;
import org.uiautomation.ios.inspector.model.Cache;
import org.uiautomation.ios.inspector.model.IDESessionModel;
import org.uiautomation.ios.inspector.views.ResourceView;
import org.uiautomation.ios.inspector.views.View;

public class ResourceController implements IDECommandController {

  private final Cache cache;

  public ResourceController(Cache cache) {
    this.cache = cache;
  }
  
  public boolean canHandle(String pathInfo) {
    return pathInfo.contains("resources");
  }

  public View handle(HttpServletRequest req)  {
    String path = req.getPathInfo();
    String pattern = "/resources/";
    int end = path.indexOf(pattern) + pattern.length();

    String resource = path.substring(end);

    InputStream is = null;
    if (resource.endsWith("screenshot.png")) {
      String session = extractSession(req.getPathInfo());
      IDESessionModel model = cache.getModel(new Session(session));
      try {
        is = model.getScreenshot();
      } catch (FileNotFoundException e) {
        is = null;
      }
      
    } else {
      is = IDEServlet.class.getResourceAsStream("/" + resource);
    }
    String mime = getMimeType(resource);
    if (is == null) {
      throw new WebDriverException("error getting resource " + req.getPathInfo()
          + req.getQueryString());
    }
    return new ResourceView(is, mime);
  }

  // TODO freynaud
  private String getMimeType(String resource) {
    if (resource.endsWith(".png")) {
      return "image/png";
    } else if (resource.endsWith(".css")) {
      return "text/css";
    } else if (resource.endsWith(".js")) {
      return "application/x-javascript";
    } else if (resource.endsWith(".jpg")) {
      return "image/jpeg";
    } else if (resource.endsWith(".gif")) {
      return "image/gif";
    }
    throw new WebDriverException("mime type NI" + resource);
  }
  
  private String extractSession(String pathInfo) {
    
    if (pathInfo.startsWith("/resources/session/")) {
      String tmp = pathInfo.replace("/resources/session/", "");
      if (tmp.contains("/")) {
        return tmp.split("/")[0];
      } else {
        return tmp;
      }
    } else {
      throw new WebDriverException("cannot extract session id from " + pathInfo);
    }
  }
  /*
     /Applications/Xcode4.5.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/Applications/iPhone Simulator.app/Contents/Resources/Devices
     
     
     /iPhone (Retina 3.5-inch).deviceinfo/chrome_halfsize.png
     /iPhone (Retina 4-inch).deviceinfo/chrome_halfsize.png
       
     /iPad.deviceinfo/portrait.png
     /iPad (Retina).deviceinfo/portrait.png
      
     

    
    
   */
  
}
