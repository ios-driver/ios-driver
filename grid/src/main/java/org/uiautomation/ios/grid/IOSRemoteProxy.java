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
package org.uiautomation.ios.grid;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.RemoteUnregisterException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.listeners.SelfHealingProxy;
import org.openqa.grid.internal.utils.HtmlRenderer;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class IOSRemoteProxy extends DefaultRemoteProxy implements SelfHealingProxy {

  private boolean restarting;
  private boolean available;
  private int sessionsServed;
  private int sessionsLimit;
  private static final Logger log = Logger.getLogger(IOSRemoteProxy.class.getName());

  private HtmlRenderer renderer = new IOSHtmlRenderer(this);

  public IOSRemoteProxy(RegistrationRequest request, Registry registry) {
    super(request, registry);
    IOSCapabilitiesMonitor iosCapabilitiesMonitor = new IOSCapabilitiesMonitor(this);
    new Thread(iosCapabilitiesMonitor).start();
    sessionsServed = 0;
    sessionsLimit = 3;
  }

  @Override
  public HtmlRenderer getHtmlRender() {
    return renderer;
  }

  @Override
  public TestSession getNewSession(Map<String, Object> requestedCapability) {
    synchronized (this) {
      if (sessionLimitReached() && !this.isBusy()) {
        setRestarting(false);
        setAvailable(false);
        doSessionLimitCleardown();
        unregister();
      }
      if (isRestarting() || !isAvailable() || this.isBusy()) {
        return null;
      } else {
        TestSession session = super.getNewSession(requestedCapability);
        if (session == null) {
          return null;
        } else {
          sessionsServed++;
          return session;
        }
      }

    }
  }

  private void doSessionLimitCleardown() {
    try {
      SeleniumGridExtrasReboot();
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  private void SeleniumGridExtrasReboot() throws IOException {
    log.info("Asking SeleniumGridExtras to reboot node");
    HttpClient client = new DefaultHttpClient();
    String url = "http://" + this.getRemoteHost().getHost() + ":" + 3000 + "/reboot";
    BasicHttpRequest r = new BasicHttpRequest("GET", url);
    HttpResponse response = client.execute(new HttpHost(this.getRemoteHost().getHost(), 3000), r);
  }

  private boolean sessionLimitReached() {
    return sessionsLimit <= sessionsServed;
  }

  public boolean isRestarting() {
    return restarting;
  }

  public void setRestarting(boolean restarting) {
    this.restarting = restarting;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public void unregister() {
    addNewEvent(new RemoteUnregisterException("Unregistering the node."));
  }
}