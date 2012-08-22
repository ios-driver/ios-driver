package org.uiautomation.ios.server;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.application.Localizable;

public class ServerSerializeExceptionTest {


  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost","-aut", SampleApps.getUICatalogFile()};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }


  @Test(expectedExceptions = IOSAutomationException.class)
  public void clientGetsServerException() throws IOSAutomationException {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = SampleApps.intlMountainsCap(Localizable.fr);
      driver = new RemoteUIADriver(url, cap);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

  }



  @AfterClass
  public void stopServer() throws Exception {
    server.stop();
  }
}
