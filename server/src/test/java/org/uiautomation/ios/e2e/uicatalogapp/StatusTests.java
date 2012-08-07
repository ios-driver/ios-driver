package org.uiautomation.ios.e2e.uicatalogapp;

import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.application.Localizable;
import org.uiautomation.ios.server.tmp.SampleApps;

public class StatusTests {

  @Test
  public void statusTest() throws InterruptedException {
    try {
      HttpClient client = HttpClientFactory.getClient();
      String url = getURL() + "/status";
      URL u = new URL(url);
      BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", url);


      HttpHost h = new HttpHost(u.getHost(), u.getPort());
      HttpResponse response = client.execute(h, r);

      JSONObject o = Helper.extractObject(response);
      System.out.println(o.toString(2));
    } catch (Exception e) {
      throw new IOSAutomationException(e);
    }

  }

  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-aut",
      SampleApps.uiCatalogCap().getApplication(), "-aut",
      SampleApps.intlMountainsCap(Localizable.fr).getApplication()};
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
