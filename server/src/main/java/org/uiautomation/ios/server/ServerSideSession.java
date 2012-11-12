package org.uiautomation.ios.server;

import java.io.File;
import java.net.URL;
import java.util.UUID;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.exceptions.SessionNotCreatedException;
import org.uiautomation.ios.mobileSafari.WebInspector;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.instruments.CommunicationChannel;
import org.uiautomation.ios.server.instruments.InstrumentsManager;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class ServerSideSession extends Session {

  private final IOSApplication application;
  private final IOSCapabilities capabilities;
  private final InstrumentsManager instruments;
  public IOSDriver driver;


  private WebInspector inspector;
  public UIADriver nativeDriver;

  private String context;
  private boolean nativeMode = true;

  ServerSideSession(IOSDriver driver, IOSCapabilities capabilities) {
    super(UUID.randomUUID().toString());
    this.driver = driver;
    this.capabilities = capabilities;

    application = driver.findMatchingApplication(capabilities);
    application.setLanguage(capabilities.getLanguage());
    if (capabilities.getSDKVersion() == null) {
      capabilities.setSDKVersion(ClassicCommands.getDefaultSDK());
    } else {
      String version = capabilities.getSDKVersion();
      if (!driver.getHostInfo().getInstalledSDKs().contains(version)) {
        throw new SessionNotCreatedException("Cannot start on version " + version + ".Installed : "
            + driver.getHostInfo().getInstalledSDKs());
      }
    }
    instruments = new InstrumentsManager(driver.getPort());

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        forceStop();
      }
    });
  }



  

  public UIADriver getNativeDriver() {
    return nativeDriver;
  }

  public IOSApplication getApplication() {
    return application;
  }

  public CommunicationChannel communication() {
    return instruments.communicate();
  }

  public void stop() {
    instruments.stop();
  }

  public void forceStop() {
    instruments.forceStop();
  }

  public File getOutputFolder() {
    return instruments.getOutput();
  }

  public InstrumentsManager getInstruments() {
    return instruments;

  }

  public void start() {
    instruments.startSession(capabilities.getDevice(), capabilities.getSDKVersion(),
        capabilities.getLocale(), capabilities.getLanguage(), application.getApplicationPath(),
        getSessionId(), capabilities.isTimeHack(), capabilities.getExtraSwitches());

    URL url = null;
    try {
      url = new URL("http://localhost:" + driver.getHostInfo().getPort() + "/wd/hub");
    } catch (Exception e) {
      e.printStackTrace();
    }
    nativeDriver = new RemoteUIADriver(url, new Session(instruments.getSessionId()));
  }


  public WebInspector getWebInspector() {
    if (inspector == null) {
      String bundleId = application.getMetadata("CFBundleIdentifier");
      try {
        this.inspector = new WebInspector(nativeDriver, bundleId,this);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return inspector;
  }

  public void setNativeContext(){
    setCurrentContext("nativeView");
  }
  
  public void setCurrentContext(String context) {
    this.context =context;
    
    if ("nativeView".equals(context)) {
      nativeMode = true;
    } else {
      nativeMode = false;
      
    }
  }
  
  public String getWindowHandle(){
    return context;
  }

  public boolean isNative() {
    return nativeMode;
  }

}
