package org.uiautomation.ios.client.uiamodels.impl;

import java.net.URL;

import org.uiautomation.ios.IOSCapabilities;

public class RemoteMobileSafariDriver extends RemoteUIADriver {

  public RemoteMobileSafariDriver(URL remoteURL, IOSCapabilities requestedCapabilities) {
    super(remoteURL, requestedCapabilities);
    switchTo().window("Web");
    // get("about:blank");
  }

}
