package org.uiautomation.ios.ide.model;

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
