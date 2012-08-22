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
package org.uiautomation.ios.server.servlet;

import javax.servlet.http.HttpServlet;

import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.instruments.IOSDriver;


public abstract class UIAScriptProxyBasedServlet extends HttpServlet {

  private static final long serialVersionUID = -8973906920199944087L;
  private IOSDriver driver;
  private IOSServerConfiguration config;

  public IOSDriver getDriver() {
    if (driver == null) {
      driver = (IOSDriver) getServletContext().getAttribute(IOSServer.DRIVER);
    }
    return driver;
  }

  public IOSServerConfiguration getServerConfig() {
    if (config == null) {
      config = (IOSServerConfiguration) getServletContext().getAttribute(IOSServer.SERVER_CONFIG);
    }
    return config;
  }
  
}
