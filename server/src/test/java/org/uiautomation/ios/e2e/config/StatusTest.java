package org.uiautomation.ios.e2e.config;

import static org.uiautomation.ios.IOSCapabilities.BUNDLE_DISPLAY_NAME;
import static org.uiautomation.ios.IOSCapabilities.BUNDLE_ID;
import static org.uiautomation.ios.IOSCapabilities.BUNDLE_NAME;
import static org.uiautomation.ios.IOSCapabilities.BUNDLE_VERSION;
import static org.uiautomation.ios.IOSCapabilities.SUPPORTED_LANGUAGES;

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
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;

public class StatusTest extends BaseIOSDriverTest {


  @Test
  public void statusTest() throws Exception {

    HttpClient client = HttpClientFactory.getClient();
    String url = getRemoteURL() + "/status";
    URL u = new URL(url);
    BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", url);

    HttpHost h = new HttpHost(u.getHost(), u.getPort());
    HttpResponse response = client.execute(h, r);

    JSONObject o = Helper.extractObject(response);

    JSONArray array = o.getJSONObject("value").getJSONArray("supportedApps");
    Assert.assertTrue(array.length() > 3); // registered app + safari

    JSONObject uicatalog = array.getJSONObject(3);

    Assert.assertEquals(uicatalog.get(BUNDLE_DISPLAY_NAME), "UICatalog");
    Assert.assertEquals(uicatalog.get(BUNDLE_ID), "com.yourcompany.UICatalog");
    Assert.assertEquals(uicatalog.get(BUNDLE_NAME), "UICatalog");
    Assert.assertEquals(uicatalog.get(BUNDLE_VERSION), "2.10");
    Assert.assertEquals(uicatalog.get("applicationPath"), SampleApps.getUICatalogIpad());

    JSONArray locales1 = uicatalog.getJSONArray(SUPPORTED_LANGUAGES);
    Assert.assertEquals(locales1.length(), 1);
    Assert.assertEquals(locales1.get(0), "en");

    JSONObject intMount = array.getJSONObject(1);

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


}
