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

package org.uiautomation.ios.grid;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.uiautomation.ios.IOSServerManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class IOSProxyStateUpdaterServlet extends RegistryBasedServlet {

  public static final String ID_MANDATORY = "requires an id parameter.";
  public static final String STATE_MANDATORY = "requires an state parameter.";
  public static final String INSTANCE_OF_MUTABLE_PROXY = "updater can only be used on mutable proxy, not on ";
  public static final String ILLEGAL_STATE = " isn't a valid state";

  public IOSProxyStateUpdaterServlet() {
    this(null);
  }


  public IOSProxyStateUpdaterServlet(Registry registry) {
    super(registry);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    process(request, response);
  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    process(request, response);
  }

  protected void process(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    JSONObject res;
    try {
      res = getResponse(request);
      if (res.getBoolean("success")) {
        response.setStatus(200);
      } else {
        response.setStatus(500);
      }
      response.getWriter().print(res);
      response.getWriter().close();
    } catch (JSONException e) {
      throw new GridException(e.getMessage());
    }

  }

  private JSONObject getResponse(HttpServletRequest request)
      throws JSONException, IOException {
    JSONObject res = new JSONObject();
    res.put("success", false);

    // check params
    String id = request.getParameter("id");
    String state = request.getParameter("state");

    if (id == null) {
      res.put("msg", ID_MANDATORY);
      return res;
    }

    if (state == null) {
      res.put("msg", STATE_MANDATORY);
      return res;
    }

    IOSServerManager.State newState = null;
    try {
      newState = IOSServerManager.State.valueOf(state);
    } catch (IllegalArgumentException ex) {
      res.put("msg", state + ILLEGAL_STATE);
      return res;
    }

    // set the new state.
    RemoteProxy proxy = getRegistry().getProxyById(id);

    if (proxy instanceof IOSMutableRemoteProxy) {
      ((IOSMutableRemoteProxy) proxy).setState(newState);
      res.put("success", true);
    } else {
      res.put("msg", INSTANCE_OF_MUTABLE_PROXY + proxy.getClass());
      return res;
    }

    return res;
  }

}
