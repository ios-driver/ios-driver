package org.uiautomation.ios.e2e.uicatalogapp;

import org.json.JSONException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATableView;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.tmp.SampleApps;

public class UIATargetTests {



  private IOSServer server;
  private static String[] args = {"-port", "4444","-host", "localhost"};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }



  @Test
  public void tapPoint() throws JSONException, Exception {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(url, SampleApps.cap());

      RemoteUIATarget target = driver.getLocalTarget();
      RemoteUIAApplication app = target.getFrontMostApp();
      RemoteUIAWindow win = app.getMainWindow();
      target.tap(20,100);



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
