package org.uiautomation.ios.ide.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class IDESessionModelImpl implements IDESessionModel {



  private final Session session;
  private IOSCapabilities caps;
  private final RemoteUIADriver driver;
  private File screenshot;

  private JSONObject elementTree;

  public IDESessionModelImpl(Session session, URL remoteURL) {
    this.session = session;
    this.screenshot = new File(session.getSessionId() + ".png");
    driver = new RemoteUIADriver(remoteURL, session);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.uiautomation.ios.ide.model.IDESessionModel2#getCapabilities()
   */
  @Override
  public IOSCapabilities getCapabilities() {
    if (caps == null) {
      caps = driver.getCapabilities();
    }
    return caps;
  }


  /*
   * (non-Javadoc)
   * 
   * @see org.uiautomation.ios.ide.model.IDESessionModel2#refresh()
   */
  @Override
  public void refresh() throws IOSAutomationException {
    screenshot.delete();
    elementTree = driver.logElementTree(screenshot, true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.uiautomation.ios.ide.model.IDESessionModel2#getSession()
   */
  @Override
  public Session getSession() {
    return session;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.uiautomation.ios.ide.model.IDESessionModel2#getScreenshot()
   */
  @Override
  public InputStream getScreenshot() throws FileNotFoundException {
    return new FileInputStream(screenshot);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.uiautomation.ios.ide.model.IDESessionModel2#getTree()
   */
  @Override
  public JSONObject getTree() {
    return elementTree;
  }

  @Override
  public Orientation getDeviceOrientation() {
    int i = elementTree.optInt("deviceOrientation");
    return Orientation.fromInterfaceOrientation(i);
  }
}
