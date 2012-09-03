package org.uiautomation.ios.ide.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.ide.model.Cache;
import org.uiautomation.ios.ide.views.RedirectView;
import org.uiautomation.ios.ide.views.View;

public class SessionGuesserController implements IDECommandController {

  private final Cache cache;


  public SessionGuesserController(Cache cache) {
    this.cache = cache;
  }

  @Override
  public boolean canHandle(String pathInfo) {
    return pathInfo.equals("/");
  }

  @Override
  public View handle(HttpServletRequest req) throws IOSAutomationException, Exception {
    Session session = getSession();
    return new RedirectView("session/" + session.getSessionId() + "/home");

  }


  private Session getSession() throws ClientProtocolException, IOException, JSONException {
    HttpClient client = HttpClientFactory.getClient();
    String url = cache.getEndPoint() + "/status";
    URL u = new URL(url);
    BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", url);


    HttpHost h = new HttpHost(u.getHost(), u.getPort());
    HttpResponse response = client.execute(h, r);

    JSONObject o = Helper.extractObject(response);
    String id = o.optString("sessionId");
    if (id == null) {
      throw new IOSAutomationException("cannot guess the sessionId for the IDE to use.");
    }
    return new Session(id);

  }

}
