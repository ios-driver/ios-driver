/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.inspector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
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

  public MockedModel(Session session, String screenshotResource, JSONObject tree,
                     IOSCapabilities cap, JSONObject status) {
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
    InputStream
        is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(screenshotResource);
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
    try {
      return new URL("http://localhost:4444/wd/hub");
    } catch (MalformedURLException e) {
      throw new RuntimeException("NI");
    }
  }

  @Override
  public JSONObject getStatus() {
    return status;
  }

}
