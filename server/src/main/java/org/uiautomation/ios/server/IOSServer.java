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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.application.ResourceCache;
import org.uiautomation.ios.server.grid.RegistrationRequest;
import org.uiautomation.ios.server.instruments.SessionsManager;
import org.uiautomation.ios.server.servlet.IOSServlet;
import org.uiautomation.ios.server.servlet.ResourceServlet;
import org.uiautomation.ios.server.servlet.UIAScriptProxyRegister;
import org.uiautomation.ios.server.servlet.UIAScriptServlet;

import com.beust.jcommander.JCommander;

public class IOSServer {

  private final String IDE_CLASS = "org.uiautomation.ios.ide.IDEServlet";

  public static final String SESSIONS_MGR = SessionsManager.class.getName();
  public static final String SERVER_CONFIG = IOSServerConfiguration.class.getName();
  public static final String RESOURCES = ResourceCache.class.getName();
  private final Server server;
  public static int port;
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

    Class<Servlet> ide = getIDEServlet();
    if (ide != null) {
      servletContextHandler.addServlet(ide, "/ide/*");
    }

    SessionsManager manager = new SessionsManager(this.options);
    servletContextHandler.setAttribute(SESSIONS_MGR, manager);
    servletContextHandler.setAttribute(SERVER_CONFIG, this.options);
    
    
    for (String app : this.options.getSupportedApps()){
      manager.getResourceCache().cacheResource(app);
    }
    
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
  
  

  private Class<Servlet> getIDEServlet() {
    try {
      Class<?> ide = Class.forName(IDE_CLASS);
      Class<Servlet> c = (Class<Servlet>) ide;
      return c;
    } catch (Exception e) {
      System.err.println("couldn't find " + IDE_CLASS);
    }
    return null;
  }



}
