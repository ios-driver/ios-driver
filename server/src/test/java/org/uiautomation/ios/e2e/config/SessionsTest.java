package org.uiautomation.ios.e2e.config;

import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;

public class SessionsTest extends BaseIOSDriverTest {
  private String url;

  private JSONArray getSessions() throws Exception {
    HttpClient client = HttpClientFactory.getClient();
    URL u = new URL(url);
    BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", url);

    HttpHost h = new HttpHost(u.getHost(), u.getPort());
    HttpResponse response = client.execute(h, r);

    Response resp = Helper.exctractResponse(response);
    String value = (String) resp.getValue();
    return new JSONArray(value);
  }

  @Test
  public void canGetSessionsFromServer() throws JSONException, Exception {
    url = getRemoteURL() + "/sessions";

    Assert.assertEquals(getSessions().length(), 0);

    RemoteUIADriver driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
    Assert.assertEquals(getSessions().length(), 1);

    JSONObject session = getSessions().getJSONObject(0);
    Assert.assertEquals(session.getString("id"), driver.getSessionId().toString());
    driver.quit();
    Assert.assertEquals(getSessions().length(), 0);
  }
}
