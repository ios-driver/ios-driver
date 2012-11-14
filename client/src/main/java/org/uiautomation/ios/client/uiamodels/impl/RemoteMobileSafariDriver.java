package org.uiautomation.ios.client.uiamodels.impl;

import org.uiautomation.ios.IOSCapabilities;

public class RemoteMobileSafariDriver extends RemoteUIADriver {

  public RemoteMobileSafariDriver(String remoteURL, IOSCapabilities requestedCapabilities) {
    super(remoteURL, requestedCapabilities);
    switchTo().window("webView");
    get("about:blank");
  }

  public RemoteMobileSafariDriver(String remoteURL) {
    super(remoteURL, IOSCapabilities.mobileSafariIpad());
    switchTo().window("webView");
    get("about:blank");
  }

}
