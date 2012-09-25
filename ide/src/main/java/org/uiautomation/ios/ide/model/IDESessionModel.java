package org.uiautomation.ios.ide.model;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.exceptions.IOSAutomationException;



public interface IDESessionModel {

  public IOSCapabilities getCapabilities();

  public void refresh() throws IOSAutomationException;

  public Session getSession();

  public InputStream getScreenshot() throws FileNotFoundException;

  public JSONObject getTree();
  
  public Orientation getDeviceOrientation();
  
  public URL getEndPoint();

}
