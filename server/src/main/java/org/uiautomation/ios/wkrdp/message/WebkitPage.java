/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.wkrdp.message;

import java.util.List;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;


public class WebkitPage implements Comparable<WebkitPage> {

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
  public String toString() {
    return "[" + WIRPageIdentifierKey + "]" + ", title:" + WIRTitleKey;
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

  public boolean isITunesAd() {
    return (WIRURLKey.contains("itunes.apple.com") && WIRURLKey.contains("style=banner"));
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

  @Override
  public int compareTo(WebkitPage webkitPage) {
    return this.getPageId() - webkitPage.getPageId();
  }

  public static boolean equals(List<WebkitPage> listA, List<WebkitPage> listB) {
    if (listA.size() != listB.size()) {
      return false;
    }
    for (WebkitPage a : listA) {
      if (!listB.contains(a)) {
        return false;
      }
    }
    return true;
  }
}
