/*
 * Copyright 2012-2014 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.servlet;

import org.uiautomation.ios.IOSServer;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ServerManagerServlet extends HttpServlet {

  public static final String STOP_GRACEFULY = "stopgracefully";
  private static final Logger log = Logger.getLogger(ServerManagerServlet.class.getName());

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      log.warning(e.toString());
    }
  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      log.warning(e.toString());
    }
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException,
             IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      log.warning(e.toString());
    }
  }


  private void process(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    if (("/"+STOP_GRACEFULY).equalsIgnoreCase(request.getPathInfo())) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            getServer().stopGracefully();
          } catch (Exception ignore) {

          }

        }
      }).start();
    } else {
      response.setStatus(404);
      response.getWriter()
          .print("path : " + request.getPathInfo() + " is not a valid URL to manage the server.");
      response.getWriter().close();
    }

  }

  private IOSServer server;

  public synchronized IOSServer getServer() {
    if (server == null) {
      server = (IOSServer) getServletContext().getAttribute(IOSServer.SERVER);
    }
    return server;
  }

}
