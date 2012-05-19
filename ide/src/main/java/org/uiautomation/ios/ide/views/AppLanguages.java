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
package org.uiautomation.ios.ide.views;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.ide.Model;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.Localizable;

public class AppLanguages implements View{
  private Model model = null;
  private String path = null;

  public AppLanguages(Model model) {
    this.model = model;
  }

  public AppLanguages(Model model, String path){
    this.model = model;
    this.path = path;
  }
  
  public void render(HttpServletResponse response) throws Exception {
    response.setContentType("application/x-javascript");
    model.setAUT(new IOSApplication(Localizable.en, path));
    List<Localizable> lang = model.getApplication().getSupportedLanguages();
    String languages = "";
    for(Localizable l : lang){
      languages += l.getName() +"|";
    }
    response.getWriter().write(languages);
  }
}
