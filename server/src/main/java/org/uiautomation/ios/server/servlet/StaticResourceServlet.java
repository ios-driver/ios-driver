package org.uiautomation.ios.server.servlet;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticResourceServlet extends HttpServlet {


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    process(request, response);
  }

  protected void process(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String resource = request.getPathInfo().replace(request.getServletPath(), "");
    if (resource.startsWith("/")) {
      resource = resource.replaceFirst("/", "");
    }
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

    try {
      IOUtils.copy(is, response.getOutputStream());
    } finally {
      response.flushBuffer();
    }
  }

}
