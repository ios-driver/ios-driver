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

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.inspector.model.Cache;
import org.uiautomation.ios.inspector.model.IDESessionModel;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MockedCache implements Cache {

  private Map<Session, IDESessionModel> cache = new HashMap<Session, IDESessionModel>();

  public MockedCache() throws Exception {
    for (Orientation o : Orientation.values()) {
      addModel(DeviceType.iphone, DeviceVariation.iPhone, o);
      addModel(DeviceType.iphone, DeviceVariation.iPhoneRetina, o);
      addModel(DeviceType.iphone, DeviceVariation.iPhoneRetina_4inch, o);
      addModel(DeviceType.iphone, DeviceVariation.iPhoneRetina_4inch_64bit, o);

      addModel(DeviceType.ipad, DeviceVariation.iPad, o);
      addModel(DeviceType.ipad, DeviceVariation.iPadRetina, o);
      addModel(DeviceType.ipad, DeviceVariation.iPadRetina_64bit, o);
    }
  }

  private void addModel(DeviceType device, DeviceVariation variation, Orientation o)
      throws Exception {
    String v = o.toString();
    if (!v.startsWith("UIA_DEVICE")) {
      v = "UIA_DEVICE_ORIENTATION_" + v;
    }
    String name = device + "_" + variation + "_" + v;

    String capability = "mock/" + device + "_" + variation + ".json";
    String screenshot = "mock/" + name + ".png";
    String logElementTree = "mock/" + name + ".json";
    String status = "mock/status.json";

    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(capability);
    StringWriter cap = new StringWriter();
    IOUtils.copy(in, cap, "UTF-8");

    in = Thread.currentThread().getContextClassLoader().getResourceAsStream(logElementTree);
    if (in == null) {
      return;
    }

    StringWriter tree = new StringWriter();
    IOUtils.copy(in, tree, "UTF-8");

    in = Thread.currentThread().getContextClassLoader().getResourceAsStream(status);
    StringWriter s = new StringWriter();
    IOUtils.copy(in, s, "UTF-8");

    Session session = new Session(name);
    JSONObject t = new JSONObject(tree.toString());
    IOSCapabilities c = new IOSCapabilities(new JSONObject(cap.toString()));

     IDESessionModel
        model =
        new MockedModel(session, screenshot, t, c, new JSONObject(s.toString()));
    cache.put(session, model);
  }

  @Override
  public URL getEndPoint() {
    throw new RuntimeException("NI");
  }

  @Override
  public IDESessionModel getModel(Session session) {
    return cache.get(session);
  }
}
