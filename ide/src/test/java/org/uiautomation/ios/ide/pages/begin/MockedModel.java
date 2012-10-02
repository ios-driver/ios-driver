package org.uiautomation.ios.ide.pages.begin;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.ide.model.IDESessionModel;

public class MockedModel implements IDESessionModel {

  private final Session session;
  private final String screenshotResource;
  private final JSONObject tree;
  private final IOSCapabilities cap;


  public MockedModel(Session session, String screenshotResource, JSONObject tree,
      IOSCapabilities cap) {
    this.session = session;
    this.tree = tree;
    this.screenshotResource = screenshotResource;
    this.cap = cap;
  }

  @Override
  public IOSCapabilities getCapabilities() {
    return cap;
  }

  @Override
  public void refresh() throws IOSAutomationException {
    // No op
  }

  @Override
  public Session getSession() {
    return session;
  }

  @Override
  public InputStream getScreenshot() {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(screenshotResource);
  }

  @Override
  public JSONObject getTree() {
    return tree;
  }
  @Override
  public Orientation getDeviceOrientation() {
   int i = tree.optInt("deviceOrientation");
   return Orientation.fromInterfaceOrientation(i);
  }

  @Override
  public URL getEndPoint() {
    throw new RuntimeException("NI");
  }

}
