package org.uiautomation.ios;

import java.net.MalformedURLException;
import java.net.URL;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public abstract class BaseIOSDriverTest {

  private IOSServer server;
  private IOSServerConfiguration config;


  @BeforeClass
  public void startServer() throws Exception {
    String[] args = {"-port", "4444", "-host", "localhost",
                     "-aut", SampleApps.getUICatalogFile(),
                     "-aut", SampleApps.getUICatalogIpad(),
                     "-aut", SampleApps.getGeocoderFile(),
                     "-aut", SampleApps.getIntlMountainsFile()
    };
    config = IOSServerConfiguration.create(args);

    server = new IOSServer(config);
    server.start();
  }

  @AfterClass
  public void stopServer() throws Exception {
    server.stop();
  }


  protected URL getRemoteURL() {
    try {
      URL remote = new URL("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub");
      return remote;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

  }
}
