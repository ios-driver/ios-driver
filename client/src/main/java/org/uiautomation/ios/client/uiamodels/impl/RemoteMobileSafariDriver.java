package org.uiautomation.ios.client.uiamodels.impl;

import java.net.URL;

import org.uiautomation.ios.IOSCapabilities;

public class RemoteMobileSafariDriver extends RemoteUIADriver {

  public RemoteMobileSafariDriver(String remoteURL, IOSCapabilities requestedCapabilities) {
    super(remoteURL, requestedCapabilities);
    switchTo().window("Web");
    //get("about:blank");
  }
  
  public RemoteMobileSafariDriver(URL remoteURL, IOSCapabilities requestedCapabilities) {
    super(remoteURL, requestedCapabilities);
    switchTo().window("Web");
    //get("about:blank");
  }

  public RemoteMobileSafariDriver(String remoteURL) {
    super(remoteURL, IOSCapabilities.mobileSafariIpad());
    switchTo().window("Web");
    //get("about:blank");
  }

 

}
