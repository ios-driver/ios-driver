package org.uiautomation.ios.e2e;

import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public class SessionsTest {
  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-aut",
      SampleApps.getUICatalogFile(), "-aut", SampleApps.getIntlMountainsFile()};
  private String url;

  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub/sessions";
    server.start();
  }

  @AfterClass
  public void stopServer() throws Exception {
    server.stop();
  }

  private JSONObject getSessions() throws Exception {
    HttpClient client = HttpClientFactory.getClient();
    URL u = new URL(url);
    BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", url);


    HttpHost h = new HttpHost(u.getHost(), u.getPort());
    HttpResponse response = client.execute(h, r);

    JSONObject o = Helper.extractObject(response);
    return o;
  }

  @Test
  public void canGetSessionsFromServer() throws JSONException, Exception {
    RemoteUIADriver driver = null;

    Assert.assertEquals(getSessions().getJSONArray("value").length(), 0);
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              SampleApps.uiCatalogCap());
      JSONArray array = getSessions().getJSONArray("value");
      Assert.assertEquals(array.length(), 1);
      JSONObject session = array.getJSONObject(0);
      Assert.assertEquals(session.getString("id"), driver.getSession().getSessionId());

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
    Assert.assertEquals(getSessions().getJSONArray("value").length(), 0);
  }
}
