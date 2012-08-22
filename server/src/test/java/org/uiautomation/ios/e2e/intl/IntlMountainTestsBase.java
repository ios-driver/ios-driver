package org.uiautomation.ios.e2e.intl;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.application.Localizable;
import org.uiautomation.ios.server.tests.SampleApps;

public class IntlMountainTestsBase {
  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost"};
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

  protected RemoteUIADriver getDriver(Localizable l) {
    return new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
        SampleApps.intlMountainsCap(l));
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
