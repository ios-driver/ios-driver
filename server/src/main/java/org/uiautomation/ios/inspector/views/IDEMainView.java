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
package org.uiautomation.ios.inspector.views;

import freemarker.template.Configuration;
import freemarker.template.Template;

import org.json.JSONArray;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.inspector.model.IDESessionModel;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public class IDEMainView implements View {

  private final IDESessionModel model;
  private final String servletPath;

  public IDEMainView(IDESessionModel model, String servletPath) {
    this.model = model;
    this.servletPath = servletPath;
  }

  @Override
  public void render(HttpServletResponse response) throws Exception {
    Configuration conf = new Configuration();
    conf.setClassForTemplateLoading(this.getClass(), "/");
    Template template = conf.getTemplate("/inspector/inspector.html");
    StringWriter writer = new StringWriter();
    Map<String, Object> map = new HashMap<>();

    List<String> cssList = new ArrayList<>();
    cssList.add(getResource("inspector/css/inspector.css"));
    cssList.add(getResource("inspector/css/ide.css"));
    cssList.add(getResource("inspector/third_party/jquery.layout.css"));
    cssList.add(getResource("inspector/third_party/jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.min.css"));

    map.put("cssList", cssList);

    List<String> jsList = new ArrayList<>();

    jsList.add(getResource("inspector/third_party/jquery-ui-1.10.3.custom/js/jquery-1.9.1.js"));
    jsList.add(getResource("inspector/third_party/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js"));
    jsList.add(getResource("inspector/layout.js"));
    jsList.add(getResource("inspector/third_party/jquery.jstree.js"));
    jsList.add(getResource("inspector/third_party/jquery.xpath.js"));
    jsList.add(getResource("inspector/prettify.js"));
    jsList.add(getResource("inspector/Logger.js"));
    jsList.add(getResource("inspector/Recorder.js"));
    jsList.add(getResource("inspector/inspector.js"));
    jsList.add(getResource("inspector/ide.js"));
    jsList.add(getResource("uiactions.js"));
    jsList.add(getResource("inspector/third_party/jquery.layout1.3.js"));

    map.put("jsList", jsList);

    DeviceType device = model.getCapabilities().getDevice();
    DeviceVariation variation = model.getCapabilities().getDeviceVariation();
    Orientation orientation = model.getDeviceOrientation();

    map.put("frame", getFrame(device, variation, orientation));

    map.put("screenshot", getScreen());

    String type = "iphone";
    if (model.getCapabilities().getDevice() == DeviceType.ipad) {
      type = "ipad";
    }

    map.put("type", type);
    map.put("variation", variation);
    map.put("orientation", orientation);

    template.process(map, writer);
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);

    response.getWriter().print(writer.toString());
  }

  private String getScreen() {
    return getResource("session/" + model.getSession().getSessionId() + "/screenshot.png");
  }

  private String getFrame(DeviceType device, DeviceVariation variation, Orientation o) {
    if (device == DeviceType.iphone) {
      switch (variation) {
        case iPhoneRetina4:
        case iPhoneRetina4_64bit:
          return getResource("inspector/images/frames/frame_iphone5_" + o.instrumentsValue() + ".png");
        default:
          return getResource("inspector/images/frames/frame_iphone_" + o.instrumentsValue() + ".png");
      }
    } else {
      return getResource("inspector/images/frames/frame_ipad_" + o.instrumentsValue() + ".jpg");
    }
  }

  private String getResource(String name) {
    return servletPath + "/resources/" + name;
  }

  private JSONObject getStatus() throws Exception {
    return model.getStatus();
  }

  private JSONObject getAppFromStatus() throws Exception {
    JSONObject status = getStatus();
    JSONArray array = status.getJSONObject("value").getJSONArray("supportedApps");
    for (int i = 0; i < array.length(); i++) {
      JSONObject jsonApp = array.getJSONObject(i);
      String other = (String) jsonApp.get("CFBundleIdentifier");
      String me = (String) model.getCapabilities().getRawCapabilities().get("CFBundleIdentifier");
      if (other.equals(me)) {
        return jsonApp;
      }
    }
    return null;
  }
}
