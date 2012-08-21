package org.uiautomation.ios.e2e.uicatalogapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
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

public class StatusTest {

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
      JSONArray array = o.getJSONObject("value").getJSONArray("supportedApps");
      Assert.assertEquals(array.length(), 2);

      JSONObject uicatalog = array.getJSONObject(1);
      Assert.assertEquals(uicatalog.get("bundleDisplayName"), "UICatalog");
      Assert.assertEquals(uicatalog.get("bundleId"), "com.yourcompany.UICatalog");
      Assert.assertEquals(uicatalog.get("bundleName"), "UICatalog");
      Assert.assertEquals(uicatalog.get("bundleVersion"), "2.10");
      Assert.assertEquals(uicatalog.get("applicationPath"), SampleApps.getUICatalogApp());

      JSONArray locales1 = uicatalog.getJSONArray("locales");
      Assert.assertEquals(locales1.length(), 1);
      Assert.assertEquals(locales1.get(0), "en");

      JSONObject intMount = array.getJSONObject(0);
      Assert.assertEquals(intMount.get("bundleId"), "com.yourcompany.InternationalMountains");
      Assert.assertEquals(intMount.get("bundleName"), "InternationalMountains");
      Assert.assertEquals(intMount.get("bundleVersion"), "1.1");
      Assert.assertEquals(intMount.get("applicationPath"), SampleApps.getIntlMountainsApp());

      JSONArray locales2 = intMount.getJSONArray("locales");
      Assert.assertEquals(locales2.length(), 3);
      List<String> all = new ArrayList<String>();
      all.add(locales2.getString(0));
      all.add(locales2.getString(1));
      all.add(locales2.getString(2));

      Assert.assertTrue(all.contains("en"));
      Assert.assertTrue(all.contains("zh"));
      Assert.assertTrue(all.contains("fr"));


    } catch (Exception e) {
      throw new IOSAutomationException(e);
    }

  }

  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-aut",
      SampleApps.getUICatalogApp(), "-aut",
      SampleApps.getIntlMountainsApp()};
  private static IOSServerConfiguration config;
  private String url;

  @BeforeClass
  public void startServer() throws Exception {
    config = IOSServerConfiguration.create(args);
    url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";
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
