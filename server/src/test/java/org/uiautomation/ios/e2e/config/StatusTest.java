package org.uiautomation.ios.e2e.config;

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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.uiautomation.ios.IOSCapabilities.BUNDLE_DISPLAY_NAME;
import static org.uiautomation.ios.IOSCapabilities.BUNDLE_ID;
import static org.uiautomation.ios.IOSCapabilities.BUNDLE_NAME;
import static org.uiautomation.ios.IOSCapabilities.BUNDLE_VERSION;
import static org.uiautomation.ios.IOSCapabilities.SUPPORTED_LANGUAGES;

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

    Map<String, JSONObject> apps = new HashMap<String, JSONObject>();
    for (int i = 0; i < array.length(); i++) {
      JSONObject a = array.getJSONObject(i);
      String name = a.optString(BUNDLE_DISPLAY_NAME);
      if (name.isEmpty()) {
        name = a.optString(BUNDLE_NAME);
      }
      apps.put(name, a);
    }
    JSONObject uicatalog = apps.get("UICatalog");

    Assert.assertEquals(uicatalog.get(BUNDLE_DISPLAY_NAME), "UICatalog");
    Assert.assertEquals(uicatalog.get(BUNDLE_ID), "com.yourcompany.UICatalog");
    Assert.assertEquals(uicatalog.get(BUNDLE_NAME), "UICatalog");
    Assert.assertEquals(uicatalog.get(BUNDLE_VERSION), "2.10");

    JSONArray locales1 = uicatalog.getJSONArray(SUPPORTED_LANGUAGES);
    Assert.assertEquals(locales1.length(), 1);
    Assert.assertEquals(locales1.get(0), "en");

    JSONObject intMount = apps.get("InternationalMountains");

    Assert.assertEquals(intMount.get(BUNDLE_ID), "com.yourcompany.InternationalMountains");
    Assert.assertEquals(intMount.get(BUNDLE_NAME), "InternationalMountains");
    Assert.assertEquals(intMount.get(BUNDLE_VERSION), "1.1");
    Assert.assertEquals(intMount.get("applicationPath"), SampleApps.getIntlMountainsFile());

    JSONArray locales2 = intMount.getJSONArray(SUPPORTED_LANGUAGES);

    int nbLanguages = 4;
    Assert.assertEquals(locales2.length(), nbLanguages);
    List<String> all = new ArrayList<String>();
    for (int i = 0; i < nbLanguages; i++) {
      all.add(locales2.getString(i));
    }

    Assert.assertTrue(all.contains("en"));
    Assert.assertTrue(all.contains("zh"));
    Assert.assertTrue(all.contains("fr"));


  }


}
