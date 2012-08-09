package org.uiautomation.ios.server.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.instruments.SessionsManager;



public class ResourceServlet extends HttpServlet {

  private SessionsManager sessionsManager;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    process(request, response);
  }

  protected void process(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String resource = request.getPathInfo().replace(request.getServletPath(), "");
    if (resource.startsWith("/")) resource = resource.replaceFirst("/", "");
    File f = getSessionsManager().getResourceCache().getResourceForKey(resource);

    try {
      IOUtils.copy(new FileInputStream(f), response.getOutputStream());
    } finally {
      response.flushBuffer();
    }
  }

  public SessionsManager getSessionsManager() {
    if (sessionsManager == null) {
      sessionsManager = (SessionsManager) getServletContext().getAttribute(IOSServer.SESSIONS_MGR);
    }
    return sessionsManager;
  }
}
