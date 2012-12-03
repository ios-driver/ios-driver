package org.uiautomation.ios.inspector;

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
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.inspector.model.Cache;
import org.uiautomation.ios.inspector.model.IDESessionModel;

public class MockedCache implements Cache {

  private Map<Session, IDESessionModel> cache = new HashMap<Session, IDESessionModel>();

  public MockedCache() throws Exception {

    for (Orientation o : Orientation.values()) {
      addModel(Device.iphone, DeviceVariation.Regular, o);
      addModel(Device.iphone, DeviceVariation.Retina35, o);
      addModel(Device.iphone, DeviceVariation.Retina4, o);

      addModel(Device.ipad, DeviceVariation.Regular, o);
      addModel(Device.ipad, DeviceVariation.Retina, o);
    }

  }

  private void addModel(Device device, DeviceVariation variation, Orientation o) throws Exception {

    String name = device + "_" + variation + "_" + o;

    String capability = "mock/" + device + "_" + variation + ".json";
    String screenshot = "mock/" + name + ".jpg";
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

    IDESessionModel model = new MockedModel(session, screenshot, t, c, new JSONObject(s.toString()));
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
