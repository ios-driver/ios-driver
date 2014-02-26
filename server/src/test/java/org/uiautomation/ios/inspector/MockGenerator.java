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

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSDriverAugmenter;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

public class MockGenerator {

  public static void main_tree(String[] args) throws Exception {
    String[] param = {"-port", "4444", "-host", "localhost"};
    IOSServerConfiguration config = IOSServerConfiguration.create(param);
    IOSServer server = new IOSServer(config);
    server.start();

    DeviceVariation[] iphoneVariations = {
        DeviceVariation.iPhone,
        DeviceVariation.iPhoneRetina,
        DeviceVariation.iPhoneRetina_4inch,
        DeviceVariation.iPhoneRetina_4inch_64bit
    };
    DeviceVariation[] ipadVariations = {
        DeviceVariation.iPad,
        DeviceVariation.iPadRetina,
        DeviceVariation.iPadRetina_64bit
    };

    generateAllVariations(DeviceType.iphone, iphoneVariations);
    generateAllVariations(DeviceType.ipad, ipadVariations);

    server.stop();
  }

  public static void main(String[] args) throws Exception {
    String[] param = {"-port", "4444", "-host", "localhost"};
    IOSServerConfiguration config = IOSServerConfiguration.create(param);
    IOSServer server = new IOSServer(config);
    server.start();

    RemoteWebDriver driver = null;

    URL url = new URL("http://localhost:4444/wd/hub");
    IOSCapabilities caps = IOSCapabilities.iphone("Safari");
    driver = new RemoteWebDriver(url, caps);

    HttpClient client = HttpClientFactory.getClient();
    String s = url + "/status";
    URL u = new URL(s);
    BasicHttpEntityEnclosingRequest
        r =
        new BasicHttpEntityEnclosingRequest("GET", u.toExternalForm());

    HttpHost h = new HttpHost(u.getHost(), u.getPort());
    HttpResponse response = client.execute(h, r);

    JSONObject o = Helper.extractObject(response);
    File base = new File("server/src/test/resources/mock");
    File status = new File(base, "status.json");

    FileWriter writer = new FileWriter(status);
    writer.write(o.toString(2));
    writer.close();
    driver.quit();
    server.stop();
  }

  private static void generateAllVariations(DeviceType type, DeviceVariation[] variations)
      throws Exception {
    for (DeviceVariation variation : variations) {
      generate(type, variation);
    }
  }

  private static void generate(DeviceType device, DeviceVariation variation) throws Exception {
    RemoteWebDriver driver = null;
    try {
      IOSCapabilities caps = IOSCapabilities.iphone("Safari");
      if (device == DeviceType.ipad) {
        caps.setDevice(DeviceType.ipad);
      }
      caps.setDeviceVariation(variation);

      driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps);
      driver.get("http://ebay.ch");
      RemoteIOSDriver d = IOSDriverAugmenter.getIOSDriver(driver);

      dumpCapabilities(device, variation, d);
      for (Orientation o : Orientation.values()) {
        try {
          d.rotate(o);
          // status
          // capabilities
          dumpElementTree(device, variation, o, d);
        } catch (WebDriverException e) {
          if (!e.getMessage()
              .contains("The orientation specified is not supported by the application")) {
            throw e;
          }
        }
      }
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  private static void dumpCapabilities(DeviceType device, DeviceVariation variation,
                                       RemoteIOSDriver d) throws IOException, JSONException {
    String capability =  device + "_" + variation + ".json";
    File base = new File("server/src/test/resources/mock");
    File caps = new File(base, capability);
    IOSCapabilities c = d.getCapabilities();

    FileWriter w = new FileWriter(caps);
    String content = new JSONObject(c.getRawCapabilities()).toString(2);
    w.write(content);
    w.close();
    System.out.println(caps.getAbsolutePath());
  }

  private static void dumpElementTree(DeviceType device, DeviceVariation variation, Orientation o,
                                      RemoteIOSDriver d)
      throws IOException, JSONException {
    File base = new File("server/src/test/resources/mock");
    String v = o.toString();
    if (!v.startsWith("UIA_DEVICE")) {
      v = "UIA_DEVICE_ORIENTATION_" + v;
    }

    String name = device + "_" + variation + "_" + v;

    String logElementTree = name + ".json";
    String screenshot = name + ".png";
    File tree = new File(base, logElementTree);
    File ss = new File(base, screenshot);

    JSONObject json = d.logElementTree(ss, true);
    FileWriter writer = new FileWriter(tree);
    writer.write(json.toString(2));
    writer.close();
  }
}
