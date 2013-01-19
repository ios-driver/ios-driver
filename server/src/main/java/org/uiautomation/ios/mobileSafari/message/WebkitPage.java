package org.uiautomation.ios.mobileSafari.message;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;


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

}
