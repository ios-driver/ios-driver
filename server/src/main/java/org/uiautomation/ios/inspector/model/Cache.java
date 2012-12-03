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

import org.uiautomation.ios.UIAModels.Session;

public interface Cache {

  
  public final String KEY = Cache.class.getCanonicalName();
  
  /**
   * the end point this cache model is conencted to. Typically http://localhost:4444/wd/hub. This
   * can be either a server or a grid hub.
   * 
   * @return
   */
  public URL getEndPoint();

  /**
   * the model reprisenting the application behing the session. The model is only a reference to
   * that application, and model().refresh(); may be necessary to have the current state.
   * 
   * @param session
   * @return
   */
  public IDESessionModel getModel(Session session);


}
