package org.uiautomation.ios;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public abstract class BaseIOSDriverTest {

  private IOSServer server;
  private IOSServerConfiguration config;
  private RemoteIOSDriver driver;


  @BeforeClass
  public void startServer() throws Exception {
    String[] args = {"-port", "4444", "-host", "localhost",
                     "-aut", SampleApps.getUICatalogFile(),
                     "-aut", SampleApps.getUICatalogIpad(),
                     "-aut", SampleApps.getGeocoderFile(),
                     "-aut", SampleApps.getIntlMountainsFile(),
                     "-aut", SampleApps.gettestNoContentFile(),
                     "-aut", SampleApps.getPPNQASampleApp(),
                     /*"-beta",*/ "-folder", "applications",
                     "-simulators"
    };
    config = IOSServerConfiguration.create(args);
    server = new IOSServer(config);
    server.start();
  }

  protected RemoteIOSDriver getDriver(IOSCapabilities cap){
    boolean simulator = true;
    cap.setCapability(IOSCapabilities.SIMULATOR,simulator);
    driver = new RemoteIOSDriver(getRemoteURL(), cap);
    return driver;
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

  public void waitForElement(WebDriver driver, org.openqa.selenium.By by, long timeOut) {

    WebElement element = (new WebDriverWait(driver, timeOut)).until(ExpectedConditions.visibilityOfElementLocated(by));

  }
}
