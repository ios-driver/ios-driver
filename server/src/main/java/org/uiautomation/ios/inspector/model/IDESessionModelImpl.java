/*
 * Copyright 2012 ios-driver committers.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uiautomation.ios.inspector.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.uiautomation.ios.client.uiamodels.impl.*;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.SessionId;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.client.uiamodels.impl.AttachRemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;

public class IDESessionModelImpl implements IDESessionModel {

  private final Session session;
  private IOSCapabilities caps;
  private final RemoteUIADriver driver;
  private File screenshot;
  private final URL remoteEndPoint;

  private JSONObject elementTree;

  public IDESessionModelImpl(Session session, URL remoteURL) {
    this.session = session;
    this.screenshot = new File(session.getSessionId() + ".png");
    this.remoteEndPoint = remoteURL;
    driver = new ServerSideNativeDriver(remoteURL, new SessionId(session.getSessionId()));
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
  public void refresh() {
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

  @Override
  public URL getEndPoint() {
    return remoteEndPoint;
  }

  @Override
  public JSONObject getStatus() {

    try {
      HttpClient client = HttpClientFactory.getClient();
      String url = getEndPoint() + "/status";
      URL u = new URL(url);
      BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", url);

      HttpHost h = new HttpHost(u.getHost(), u.getPort());
      HttpResponse response = client.execute(h, r);

      JSONObject o = Helper.extractObject(response);

      return o;
    } catch (Exception e) {
      throw new WebDriverException(e.getMessage(), e);
    }

  }

}
