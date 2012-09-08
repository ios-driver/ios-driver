package org.uiautomation.ios.ide.pages.begin;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.ide.model.Cache;
import org.uiautomation.ios.ide.model.IDESessionModel;
import org.uiautomation.ios.server.application.Localizable;

public class MockedCache implements Cache {

  private Map<Session, IDESessionModel> cache = new HashMap<Session, IDESessionModel>();

  public MockedCache() throws Exception {
    addModel("eBay", IOSDevice.iPadSimulator, Localizable.fr,
        Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPELEFT);
    addModel("eBay", IOSDevice.iPadSimulator, Localizable.fr,
        Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT);
    addModel("eBay", IOSDevice.iPadSimulator, Localizable.fr,
        Orientation.UIA_DEVICE_ORIENTATION_PORTRAIT);
    addModel("eBay", IOSDevice.iPadSimulator, Localizable.fr,
        Orientation.UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN);

  }


  private void addModel(String bundleName, IOSDevice device, Localizable l, Orientation o)
      throws Exception {
    String name = bundleName + "_" + l + "_" + o;
    String p = "iphone";
    if (device == IOSDevice.iPadSimulator) {
      p = "ipad";
    }
    String json = "mock/" + p + "/" + name + ".json";
    String screenshot = "mock/" + p + "/" + name + ".png";
    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(json);
    StringWriter out = new StringWriter();
    IOUtils.copy(in, out, "UTF-8");

    JSONObject tree = new JSONObject(out.toString());
    Session session = new Session(name + p);
    IOSCapabilities cap = new IOSCapabilities();
    cap.setDevice(device);
    cap.setLocale(l.getName());
    cap.setBundleName(bundleName);

    IDESessionModel model = new MockedModel(session, screenshot, tree, cap);
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
