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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.ide.Model;
import org.uiautomation.ios.ide.views.RedirectView;
import org.uiautomation.ios.ide.views.View;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.LanguageDictionary;
import org.uiautomation.ios.server.application.Localizable;


public class StartController extends BaseController {

  public StartController(Model model) {
    super(model);
  }

  private final String url = "http://localhost:5555/wd/hub";


  public boolean canHandle(String pathInfo) {
    return pathInfo.contains("/start");
  }

  // TODO freynaud lock here ?
  public View handle(HttpServletRequest req) throws IOSAutomationException {
    Map<String, Object> res = new HashMap<String, Object>();

    res.put(IOSCapabilities.DEVICE, req.getParameter(IOSCapabilities.DEVICE));
    res.put(IOSCapabilities.LOCALE, req.getParameter(IOSCapabilities.LOCALE));
    res.put(IOSCapabilities.LANGUAGE, req.getParameter(IOSCapabilities.LANGUAGE));
    res.put(IOSCapabilities.SDK_VERSION, req.getParameter(IOSCapabilities.SDK_VERSION));
    res.put(IOSCapabilities.AUT, req.getParameter(IOSCapabilities.AUT));
    res.put(IOSCapabilities.TIME_HACK, req.getParameter(IOSCapabilities.TIME_HACK));
    boolean qa = false;
    if (qa) {
      res.put(IOSCapabilities.IOS_SWITCHES, Arrays.asList(new String[] {"useQA", "YES"}));
    }
    RemoteUIADriver d = new RemoteUIADriver(url, res);
    getModel().setDriver(d);
    
    Localizable current =
        new LanguageDictionary((String) res.get(IOSCapabilities.LANGUAGE)).getLanguage();
    IOSApplication app = new IOSApplication(current, (String)res.get(IOSCapabilities.AUT));
    app.loadAllContent();
    getModel().setAUT(app);
    getModel().refresh();
    return new RedirectView("browse");

  }


}
