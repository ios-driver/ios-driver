package org.uiautomation.ios.ide.controllers;

import javax.servlet.http.HttpServletRequest;

import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.ide.views.HomeView;
import org.uiautomation.ios.ide.views.View;

public class HomeController implements IDECommandController {

  @Override
  public boolean canHandle(String pathInfo) {
    return pathInfo.contains("index");
  }

  @Override
  public View handle(HttpServletRequest req) throws IOSAutomationException, Exception {
    return new HomeView();
  }

}
