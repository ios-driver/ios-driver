package org.uiautomation.ios.e2e;

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
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;


import static org.uiautomation.ios.IOSCapabilities.*;

public class StatusTest {

  @Test
  public void statusTest() throws  Exception {

    HttpClient client = HttpClientFactory.getClient();
    String url = getURL() + "/status";
    URL u = new URL(url);
    BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", url);


    HttpHost h = new HttpHost(u.getHost(), u.getPort());
    HttpResponse response = client.execute(h, r);

    JSONObject o = Helper.extractObject(response);

    
    JSONArray array = o.getJSONObject("value").getJSONArray("supportedApps");
    Assert.assertEquals(array.length(), 2);

    JSONObject uicatalog = array.getJSONObject(1);
   
    
    Assert.assertEquals(uicatalog.get(BUNDLE_DISPLAY_NAME), "UICatalog");
    Assert.assertEquals(uicatalog.get(BUNDLE_ID), "com.yourcompany.UICatalog");
    Assert.assertEquals(uicatalog.get(BUNDLE_NAME), "UICatalog");
    Assert.assertEquals(uicatalog.get(BUNDLE_VERSION), "2.10");
    Assert.assertEquals(uicatalog.get("applicationPath"), SampleApps.getUICatalogFile());

    JSONArray locales1 = uicatalog.getJSONArray(SUPPORTED_LANGUAGES);
    Assert.assertEquals(locales1.length(), 1);
    Assert.assertEquals(locales1.get(0), "en");

    JSONObject intMount = array.getJSONObject(0);
   
    Assert.assertEquals(intMount.get(BUNDLE_ID), "com.yourcompany.InternationalMountains");
    Assert.assertEquals(intMount.get(BUNDLE_NAME), "InternationalMountains");
    Assert.assertEquals(intMount.get(BUNDLE_VERSION), "1.1");
    Assert.assertEquals(intMount.get("applicationPath"), SampleApps.getIntlMountainsFile());

    JSONArray locales2 = intMount.getJSONArray(SUPPORTED_LANGUAGES);
    Assert.assertEquals(locales2.length(), 3);
    List<String> all = new ArrayList<String>();
    all.add(locales2.getString(0));
    all.add(locales2.getString(1));
    all.add(locales2.getString(2));

    Assert.assertTrue(all.contains("en"));
    Assert.assertTrue(all.contains("zh"));
    Assert.assertTrue(all.contains("fr"));



  }

  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-aut",
      SampleApps.getUICatalogFile(), "-aut", SampleApps.getIntlMountainsFile()};
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
