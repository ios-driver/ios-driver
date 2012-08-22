package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public abstract class UICatalogTestsBase {

  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-aut",
      SampleApps.getUICatalogFile()};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }

  protected String getURL() {
    return url;
  }

  protected RemoteUIADriver getDriver() {
    return new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
        SampleApps.uiCatalogCap());

  }

  protected RemoteUIAWindow getMainWindow(RemoteUIADriver driver) {
    RemoteUIATarget target = driver.getLocalTarget();
    RemoteUIAApplication app = target.getFrontMostApp();
    RemoteUIAWindow window = app.getMainWindow();
    return window;
  }

  @AfterClass
  public void stopServer() throws Exception {
    server.stop();
  }
}
