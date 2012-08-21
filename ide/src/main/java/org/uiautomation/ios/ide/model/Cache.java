package org.uiautomation.ios.ide.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.uiautomation.ios.UIAModels.Session;

public class Cache {

  private final URL REMOTE;
  private Map<Session, IDESessionModel> models = new ConcurrentHashMap<Session, IDESessionModel>();

  public Cache() {
    URL u = null;
    try {
      u = new URL("http://localhost:5556/wd/hub");
    } catch (MalformedURLException e) {
      // ignore.
    }
    REMOTE = u;
  }

  public synchronized IDESessionModel getModel(Session session) {
    IDESessionModel model = models.get(session);
    if (model == null) {
      model = new IDESessionModel(session, REMOTE);
      model.refresh();
      models.put(session, model);
    }
    return model;
  }

}
