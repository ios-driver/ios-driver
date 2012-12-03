package org.uiautomation.ios.inspector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.inspector.model.IDESessionModel;

public class MockedModel implements IDESessionModel {

  private final Session session;
  private final String screenshotResource;
  private final JSONObject tree;
  private final JSONObject status;
  private final IOSCapabilities cap;

  public MockedModel(Session session, String screenshotResource, JSONObject tree, IOSCapabilities cap, JSONObject status) {
    this.session = session;
    this.tree = tree;
    this.screenshotResource = screenshotResource;
    this.cap = cap;
    this.status = status;
  }

  @Override
  public IOSCapabilities getCapabilities() {
    return cap;
  }

  @Override
  public void refresh() {
    // No op
  }

  @Override
  public Session getSession() {
    return session;
  }

  @Override
  public InputStream getScreenshot() {
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(screenshotResource);
    if (is == null) {
      try {
        return new FileInputStream(screenshotResource);
      } catch (FileNotFoundException e) {
        return null;
      }
    } else {
      return is;
    }

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

  @Override
  public JSONObject getStatus() {
    return status;
  }

}
