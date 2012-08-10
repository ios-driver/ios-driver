package org.uiautomation.ios.ide.model;

import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.Session;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.application.IOSApplication;

public class IDESessionModel {
  
  private final Session session;
  private final RemoteUIADriver driver;
  private IOSApplication app;
  
  private JSONObject elementTree;
  
  public IDESessionModel(Session session,URL remoteURL){
    this.session = session;
    driver =  new RemoteUIADriver(remoteURL, session);
  }
  
  public void refresh() throws IOSAutomationException {
    elementTree = driver.logElementTree();
  }

  public Session getSession() {
   return session;
  }

}
