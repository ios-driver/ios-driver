package org.uiautomation.ios.ide.model;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.uiautomation.ios.UIAModels.Session;

public class Cache {

  private final URL remote;
  private Map<Session, IDESessionModel> models = new ConcurrentHashMap<Session, IDESessionModel>();

  /**
   * webdriver url. http://localhost:4444/wd/hub for instance.
   * @param endpoint
   */
  public Cache(URL endpoint) {
    remote = endpoint;
  }

  public synchronized IDESessionModel getModel(Session session) {
    IDESessionModel model = models.get(session);
    if (model == null) {
      model = new IDESessionModel(session, remote);
      model.refresh();
      models.put(session, model);
    }
    return model;
  }

}
