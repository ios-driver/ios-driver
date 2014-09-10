/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.uiautomation.ios.inspector.model.Cache;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IDEServer {

  private final Server server;
  private final ServletContextHandler servletContextHandler;
  private static final Logger log = Logger.getLogger(IDEServer.class.getName());

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

  public void init() {
    servletContextHandler.addServlet(IDEServlet.class, "/*");

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          server.stop();
        } catch (Exception e) {
          log.log(Level.SEVERE,"error in shutdown hook servlet context",e);
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
    servletContextHandler = new ServletContextHandler(server, "/inspector", true, false);
  }
}
