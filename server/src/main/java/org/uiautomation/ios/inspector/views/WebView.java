package org.uiautomation.ios.inspector.views;

import org.openqa.selenium.WebDriverException;

import javax.servlet.http.HttpServletResponse;

public class WebView implements View {

  private String html;

  public WebView(String html) {
    this.html = html;
  }

  @Override
  public void render(HttpServletResponse response) throws Exception {
    try {
      StringBuilder b = new StringBuilder();
      b.append(html);

      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      response.setStatus(200);

      response.getWriter().print(b.toString());
    } catch (Exception e) {
      throw new WebDriverException(e);
    }

  }

}
