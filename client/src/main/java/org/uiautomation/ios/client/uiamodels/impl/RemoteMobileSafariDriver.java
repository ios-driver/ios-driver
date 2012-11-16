package org.uiautomation.ios.client.uiamodels.impl;

import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.internal.WebElementToJsonConverter;
import org.uiautomation.ios.IOSCapabilities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class RemoteMobileSafariDriver extends RemoteUIADriver {

  public RemoteMobileSafariDriver(String remoteURL, IOSCapabilities requestedCapabilities) {
    super(remoteURL, requestedCapabilities);
    switchTo().window("Web");
    get("about:blank");
  }

  public RemoteMobileSafariDriver(String remoteURL) {
    super(remoteURL, IOSCapabilities.mobileSafariIpad());
    switchTo().window("Web");
    get("about:blank");
  }

 

}
