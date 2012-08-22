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

package org.uiautomation.ios.server;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.grid.RegistrationRequest;
import org.uiautomation.ios.server.instruments.IOSDriver;
import org.uiautomation.ios.server.servlet.IOSServlet;
import org.uiautomation.ios.server.servlet.ResourceServlet;
import org.uiautomation.ios.server.servlet.UIAScriptProxyRegister;
import org.uiautomation.ios.server.servlet.UIAScriptServlet;

import com.beust.jcommander.JCommander;

public class IOSServer {

  public static final String DRIVER = IOSDriver.class.getName();
  private final Server server;
  private int port;
  public static final boolean debugMode = true;
  private IOSServerConfiguration options;


  public static void main(String[] args) throws Exception {
    IOSServerConfiguration options = new IOSServerConfiguration();
    new JCommander(options, args);

    IOSServer server = new IOSServer(options);
    server.start();
    if (options.getRegistrationURL() != null) {
      RegistrationRequest request =
          new RegistrationRequest(options.getRegistrationURL(), options.getHost(),
              options.getPort(), options.getSupportedApps());
      request.registerToHub();
    }
  }

  public void init() throws IOSAutomationSetupException {
    ServletContextHandler servletContextHandler =
        new ServletContextHandler(server, "/wd/hub", true, false);
    servletContextHandler.addServlet(UIAScriptProxyRegister.class, "/uiascriptproxy/register/*");
    servletContextHandler.addServlet(UIAScriptServlet.class, "/uiascriptproxy/*");
    servletContextHandler.addServlet(IOSServlet.class, "/*");
    servletContextHandler.addServlet(ResourceServlet.class, "/resources/*");

    IOSDriver driver = new IOSDriver(port);
    for (String app : this.options.getSupportedApps()) {
      driver.addSupportedApplication(new IOSApplication(null, app));
    }

    servletContextHandler.setAttribute(DRIVER, driver);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          server.stop();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

  }

  public void start() throws Exception {
    init();
    if (!server.isRunning()) {
      server.start();
    }
  }


  public void stop() throws Exception {
    server.stop();
  }

  public IOSServer(IOSServerConfiguration options) throws IOSAutomationSetupException {
    this.options = options;
    this.port = options.getPort();
    server = new Server(new InetSocketAddress("localhost", options.getPort()));

  }







}
