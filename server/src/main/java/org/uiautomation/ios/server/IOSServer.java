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

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.grid.RegistrationRequest;
import org.uiautomation.ios.server.servlet.IOSServlet;
import org.uiautomation.ios.server.servlet.ResourceServlet;
import org.uiautomation.ios.server.servlet.UIAScriptProxyRegister;
import org.uiautomation.ios.server.servlet.UIAScriptServlet;

import com.beust.jcommander.JCommander;

public class IOSServer {

  public static final String DRIVER = IOSDriver.class.getName();
  private Server server;
  private int port;
  public static final boolean debugMode = true;
  private IOSServerConfiguration options;


  public static void main(String[] args) throws Exception {

    IOSServer server = new IOSServer(args);
    server.start();
    IOSServerConfiguration options = server.getOptions();
    if (options.getRegistrationURL() != null) {
      RegistrationRequest request =
          new RegistrationRequest(options.getRegistrationURL(), options.getHost(),
              options.getPort(), options.getSupportedApps());
      request.registerToHub();
    }
  }

  public IOSServer(IOSServerConfiguration options) throws IOSAutomationSetupException {
    init(options);

  }

  public IOSServer(String[] args) {
    init(args);
  }

  public IOSServerConfiguration getOptions() {
    return options;
  }

  private void init(String[] args) {
    IOSServerConfiguration options = new IOSServerConfiguration();
    new JCommander(options, args);
    init(options);
  }

  private void init(IOSServerConfiguration options) throws IOSAutomationSetupException {
    this.options = options;
    this.port = options.getPort();
    server = new Server(new InetSocketAddress("0.0.0.0", options.getPort()));


    ServletContextHandler wd = new ServletContextHandler(server, "/wd/hub", true, false);
    wd.addServlet(UIAScriptProxyRegister.class, "/uiascriptproxy/register/*");
    wd.addServlet(UIAScriptServlet.class, "/uiascriptproxy/*");
    wd.addServlet(IOSServlet.class, "/*");
    wd.addServlet(ResourceServlet.class, "/resources/*");



    ServletContextHandler extra = new ServletContextHandler(server, "/", true, false);
    try {
      String ide = "org.uiautomation.ios.ide.IDEServlet";
      Class<?> c = Class.forName(ide);
      Class<Servlet> r = (Class<Servlet>) c;

      extra.addServlet(r, "/ide/*");
      
    } catch (Exception e) {
      System.err.println("couldn't find ide servlet");
    }

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] {wd, extra});
    server.setHandler(handlers);

    IOSDriver driver = new IOSDriver(port);
    for (String app : this.options.getSupportedApps()) {
      driver.addSupportedApplication(new IOSApplication(app));
    }

    wd.setAttribute(DRIVER, driver);
    extra.setAttribute(DRIVER, driver);

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
    if (!server.isRunning()) {
      server.start();
    }
  }


  public void stop() throws Exception {
    server.stop();
  }



}
