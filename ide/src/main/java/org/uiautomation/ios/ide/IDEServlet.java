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
package org.uiautomation.ios.ide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.uiautomation.ios.ide.controllers.AttachController;
import org.uiautomation.ios.ide.controllers.BeginController;
import org.uiautomation.ios.ide.controllers.DebugController;
import org.uiautomation.ios.ide.controllers.HomeController;
import org.uiautomation.ios.ide.controllers.IDECommandController;
import org.uiautomation.ios.ide.controllers.IDEController;
import org.uiautomation.ios.ide.controllers.NoActionController;
import org.uiautomation.ios.ide.controllers.NotImplementedIDEController;
import org.uiautomation.ios.ide.controllers.RefreshController;
import org.uiautomation.ios.ide.controllers.ResourceController;
import org.uiautomation.ios.ide.controllers.StartController;
import org.uiautomation.ios.ide.controllers.StopController;
import org.uiautomation.ios.ide.controllers.UserActionController;
import org.uiautomation.ios.ide.controllers.getLanguagesController;
import org.uiautomation.ios.ide.views.JSTreeJSON;
import org.uiautomation.ios.ide.views.View;
import org.uiautomation.ios.server.servlet.UIAScriptProxyBasedServlet;


public class IDEServlet extends UIAScriptProxyBasedServlet {

  // TODO freynaud
  String path;
  String url;
  // JSONObject cache = null;
  private final Model model = new Model();

  private static final long serialVersionUID = 333974658353413397L;

  private final List<IDECommandController> controllers = new ArrayList<IDECommandController>();

  @Override
  public void init() throws ServletException {
    super.init();

    controllers.add(new IDEController());
    controllers.add(new ResourceController());
    /*controllers.add(new HomeController());
    
    controllers.add(new AttachController(model));
    controllers.add(new StartController(model));
    controllers.add(new RefreshController(model));
   
    controllers.add(new StopController(model));
    controllers.add(new UserActionController(model));
    controllers.add(new BeginController(model, getServerConfig()));
    controllers.add(new DebugController(model));
    controllers.add(new getLanguagesController(model));

    controllers.add(new NoActionController(model, "/tree", new JSTreeJSON(model)));*/

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
    response.setCharacterEncoding("UTF-8");
    try {
      IDECommandController controller = getController(req.getPathInfo());
      View view = controller.handle(req);
      view.render(response);
    } catch (Exception e) {
      // TODO freynaud
      throw new RuntimeException(e);
    }
  }



  private IDECommandController getController(String pathInfo) {
    for (IDECommandController c : controllers) {
      if (c.canHandle(pathInfo)) {
        return c;
      }
    }
    return new NotImplementedIDEController(model);
  }



}
