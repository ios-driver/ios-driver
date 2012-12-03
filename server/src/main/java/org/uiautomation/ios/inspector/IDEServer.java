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
package org.uiautomation.ios.inspector;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.uiautomation.ios.inspector.model.Cache;

public class IDEServer {

  private final Server server;
  private final ServletContextHandler servletContextHandler;

  public static void main(String[] args) throws Exception {

    int port = 5555;
    if (args.length == 1) {
      port = Integer.parseInt(args[0]);
    }

    IDEServer server = new IDEServer(port);
    server.start();
  }

  public void setCache(Cache c) {
    servletContextHandler.setAttribute(Cache.KEY, c);
  }

  public void init()  {

    servletContextHandler.addServlet(IDEServlet.class, "/*");

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

  public IDEServer(int port) {
    server = new Server(new InetSocketAddress("localhost", port));
    servletContextHandler = new ServletContextHandler(server, "/ide", true, false);
  }

}
