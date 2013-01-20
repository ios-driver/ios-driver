package org.uiautomation.ios.mobileSafari.message;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;


public class WebkitPage {

  private final String WIRTitleKey;
  private final String WIRURLKey;
  private final int WIRPageIdentifierKey;
  private final String WIRConnectionIdentifierKey;

  public WebkitPage(NSDictionary page) {
    WIRTitleKey = page.objectForKey("WIRTitleKey").toString();
    WIRURLKey = page.objectForKey("WIRURLKey").toString();
    WIRPageIdentifierKey = Integer.parseInt(page.objectForKey("WIRPageIdentifierKey").toString());

    NSObject cnx = page.objectForKey("WIRConnectionIdentifierKey");
    WIRConnectionIdentifierKey = cnx == null ? null : cnx.toString();

  }

  public int getPageId() {
    return WIRPageIdentifierKey;
  }

  public String getTitle() {
    return WIRTitleKey;
  }

  public String getURL() {
    return WIRURLKey;
  }

  public String getConnection() {
    return WIRConnectionIdentifierKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    WebkitPage that = (WebkitPage) o;

    if (WIRPageIdentifierKey != that.WIRPageIdentifierKey) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return WIRPageIdentifierKey;
  }

  public JSONObject asJSON() {
    JSONObject json = null;
    try {
      json = new JSONObject()
          .put("title", WIRTitleKey)
          .put("url", WIRURLKey)
          .put("id", WIRPageIdentifierKey)
          .put("connection", WIRConnectionIdentifierKey);
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
    return json;
  }
}
