package org.uiautomation.ios.server.command.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.server.command.BuildInfo;
import org.uiautomation.ios.server.instruments.ClassicCommands;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class ServerStatus extends BaseCommandHandler {

  private final IOSServerConfiguration config;
  public ServerStatus(SessionsManager sessionsManager, WebDriverLikeRequest request) {
    super(sessionsManager, request);
    this.config = getSessionsManager().getServerConfig();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.uiautomation.ios.server.command.Handler#handle()
   */
  @Override
  public WebDriverLikeResponse handle() throws Exception {
    JSONObject res = new JSONObject();
 
    res.put("os",new JSONObject()
          .put("name", System.getProperty("os.name"))
          .put("arch", System.getProperty("os.arch"))
          .put("version", System.getProperty("os.version")));
    
    res.put("java", new JSONObject()
          .put("version", System.getProperty("java.version")));
    
    List<String> sdks = ClassicCommands.getInstalledSDKs();
    
    res.put("ios", new JSONObject()
    .put("simulatorVersion", sdks.get(sdks.size()-1)));
    
    JSONArray supportedApps = new JSONArray();
    for (String path : config.getSupportedApps()){
      JSONObject app = new JSONObject();
      app.put("path", path);
      IOSApplication a = new IOSApplication(null, path);
      
      app.put("locales",a.getSupportedLanguages());
      app.put("bundleName",a.getBundleName());
      app.put("bundleDisplayName",a.getBundleDisplayName());
      app.put("bundleId",a.getBundleId());
      app.put("bundleVersion",a.getBundleVersion());
      app.put("version",a.getVersion());
      supportedApps.put(app);
    }
    
   res.put("supportedApps",supportedApps);
    
   res.put("build",new JSONObject()
       .put("version", BuildInfo.getAttribute("version"))
       .put("time", BuildInfo.getAttribute("buildTimestamp"))
       .put("revision", BuildInfo.getAttribute("sha")));
   

    
    
    return new WebDriverLikeResponse(null, 0, res);
     }
}
