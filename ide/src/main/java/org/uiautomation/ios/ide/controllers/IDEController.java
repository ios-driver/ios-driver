package org.uiautomation.ios.ide.controllers;

import javax.servlet.http.HttpServletRequest;

import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.ide.model.Cache;
import org.uiautomation.ios.ide.model.IDESessionModel;
import org.uiautomation.ios.ide.views.IDEMainView;
import org.uiautomation.ios.ide.views.View;

public class IDEController implements IDECommandController {

  private final Cache cache;

  public IDEController(Cache cache) {
    this.cache = cache;
  }

  @Override
  public boolean canHandle(String pathInfo) {
    boolean ok = pathInfo.startsWith("/session/") && pathInfo.endsWith("/home");
    return ok;
  }

  @Override
  public View handle(HttpServletRequest req) throws IOSAutomationException, Exception {
    final Session s = new Session(extractSession(req.getPathInfo()));
    IDESessionModel model = cache.getModel(s);
    return new IDEMainView(model, req.getContextPath() + req.getServletPath());
  }

  private String extractSession(String pathInfo) {

    if (pathInfo.startsWith("/session/")) {
      String tmp = pathInfo.replace("/session/", "");
      if (tmp.contains("/")) {
        return tmp.split("/")[0];
      } else {
        return tmp;
      }
    } else {
      throw new IOSAutomationException("cannot extract session id from " + pathInfo);
    }
  }
}
