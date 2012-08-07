package org.uiautomation.ios.e2e.uicatalogapp;

import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class StatusTests extends UICatalogTestsBase {

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

}
