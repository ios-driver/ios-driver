package org.uiautomation.ios.ide.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.ide.Model;
import org.uiautomation.ios.ide.views.JSONView;
import org.uiautomation.ios.ide.views.View;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.Localizable;

public class getLanguagesController extends BaseController {

  public getLanguagesController(Model model) {
    super(model);
  }

  @Override
  public boolean canHandle(String pathInfo) {
    return pathInfo.contains("getLanguages");
  }

  @Override
  public View handle(HttpServletRequest req) throws IOSAutomationException, Exception {
    String path = req.getParameter("app");
    IOSApplication app = new IOSApplication(Localizable.en, path);
    List<Localizable> supporteds = app.getSupportedLanguages();
    JSONArray all = new JSONArray();
    for (Localizable l : supporteds) {
      all.put(l);
    }
   
    return new JSONView(all);
  }



}
