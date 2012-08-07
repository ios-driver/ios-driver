package org.uiautomation.ios.server.command.impl;

import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class ServerStatus extends BaseCommandHandler {

  public ServerStatus(SessionsManager sessionsManager, WebDriverLikeRequest request) {
    super(sessionsManager, request);
  }

  /* (non-Javadoc)
   * @see org.uiautomation.ios.server.command.Handler#handle()
   */
  @Override
  public WebDriverLikeResponse handle() throws Exception {
    JSONObject res = new JSONObject();
    JSONObject os = new JSONObject();
    os.put("arch", "64bits");
    res.put("os", os);
    
    return new WebDriverLikeResponse(null,0,res);
    //build   object  
    //build.version   string  A generic release label (i.e. "2.0rc3")
    //build.revision  string  The revision of the local source control client from which the server was built
    //build.time  string  A timestamp from when the server was built.
    //os  object  
    //os.arch     string  The current system architecture.
    //os.name     string  The name of the operating system the server is currently running on: "windows", "linux", etc.
    //os.version  string  The operating system version. 
  }

}
