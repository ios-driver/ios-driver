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

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.uiautomation.ios.UIAModels.Session;

public class CacheDefaultImpl implements Cache {

  private final URL remote;
  private Map<Session, IDESessionModel> models = new ConcurrentHashMap<Session, IDESessionModel>();

  /**
   * webdriver url. http://localhost:4444/wd/hub for instance.
   * 
   * @param endpoint
   */
  public CacheDefaultImpl(URL endpoint) {
    remote = endpoint;
  }

  @Override
  public URL getEndPoint() {
    return remote;
  }

  @Override
  public synchronized IDESessionModel getModel(Session session) {
    IDESessionModel model = models.get(session);
    if (model == null) {
      model = new IDESessionModelImpl(session, remote);
      models.put(model.getSession(), model);
    }
    return model;
  }

}
