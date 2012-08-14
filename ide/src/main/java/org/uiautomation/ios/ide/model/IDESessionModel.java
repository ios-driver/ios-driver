package org.uiautomation.ios.ide.model;

import java.io.File;
import java.net.URL;

import org.json.JSONObject;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.Session;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.application.IOSApplication;

public class IDESessionModel {



  private final Session session;
  private final RemoteUIADriver driver;
  private IOSApplication app;
  private File screenshot;

  private JSONObject elementTree;

  public IDESessionModel(Session session, URL remoteURL) {
    this.session = session;
    this.screenshot = new File(session.getSessionId() + ".png");
    driver = new RemoteUIADriver(remoteURL, session);
  }

  public void refresh() throws IOSAutomationException {
    screenshot.delete();
    elementTree = driver.logElementTree(screenshot,true);
    System.out.println(screenshot.getAbsolutePath());
  }

  public Session getSession() {
    return session;
  }

  public File getScreenshot() {
    return screenshot;
  }
  
  public JSONObject getTree(){
    return elementTree;
  }
}
