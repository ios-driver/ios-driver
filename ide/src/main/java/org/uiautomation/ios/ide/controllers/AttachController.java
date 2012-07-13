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
package org.uiautomation.ios.ide.controllers;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.Session;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.ide.Model;
import org.uiautomation.ios.ide.views.RedirectView;
import org.uiautomation.ios.ide.views.View;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.Localizable;
import org.uiautomation.ios.server.tmp.SampleApps;

// TODO freynaud
public class AttachController extends BaseController {



  public AttachController(Model model) {
    super(model);
  }

  public boolean canHandle(String pathInfo) {
    return pathInfo.contains("attach");
  }

  public View handle(HttpServletRequest req) throws IOSAutomationException, Exception {
    String session = req.getParameter("sessionId");
    // TODO freynaud
    URL u = new URL("http://localhost:4444/wd/hub");
    RemoteUIADriver d = new RemoteUIADriver(u, new Session(session));
    getModel().setDriver(d);

    IOSCapabilities cap = d.getCapabilities();

    Localizable current = Localizable.valueOf(cap.getLanguage());
    IOSApplication app = new IOSApplication(current, cap.getApplication());
    app.loadAllContent();
    getModel().setAUT(app);
    getModel().refresh();

    return new RedirectView("browse");
  }

}
