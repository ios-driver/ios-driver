/*
 * Copyright 2012-2014 eBay Software Foundation and ios-driver committers
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
import org.apache.http.message.BasicHttpRequest;
import org.openqa.grid.common.exception.RemoteNotReachableException;
import org.openqa.grid.common.exception.RemoteUnregisterException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.utils.HtmlRenderer;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.servlet.ServerManagerServlet;

import java.util.Map;
import java.util.logging.Logger;

public class IOSMutableRemoteProxy extends DefaultRemoteProxy {

  public static final IOSServerManager.State DEFAULT_STATE = IOSServerManager.State.running;
  private IOSServerManager.State state = DEFAULT_STATE;
  private static final Logger log = Logger.getLogger(DefaultRemoteProxy.class.getName());

  public IOSMutableRemoteProxy(org.openqa.grid.common.RegistrationRequest request,
                               Registry registry) {
    super(request, registry);


    IOSCapabilitySyncronizer syncronizer = new IOSCapabilitySyncronizer(this);
    syncronizer.sync();
  }



  public void setState(IOSServerManager.State state) {
    synchronized (this) {
      this.state = state;
    }

    if (getState() != IOSServerManager.State.running) {
      stopPolling();
      addNewEvent(new RemoteNotReachableException("Not not running"));
    }

    if (getState() == IOSServerManager.State.stopped) {
      addNewEvent(new RemoteUnregisterException("Unregistering the node."));
    }
  }

  public IOSServerManager.State getState() {
    synchronized (this) {
      return state;
    }
  }

  public void stopGracefully() throws Exception {
    setState(IOSServerManager.State.stopping);
    HttpClient client = getHttpClientFactory().getGridHttpClient(10000, 10000);

    BasicHttpRequest r = new BasicHttpRequest("GET", getRemoteHost().toExternalForm() + "/grid/manage/" + ServerManagerServlet.STOP_GRACEFULY);

    HttpHost host = new HttpHost(getRemoteHost().getHost(), getRemoteHost().getPort());
    HttpResponse response = client.execute(host, r);
    int code = response.getStatusLine().getStatusCode();
    if (code != 200) {
      log.warning(ServerManagerServlet.STOP_GRACEFULY + " command returned " + code);
    }
  }

  @Override
  public TestSession getNewSession(Map<String, Object> requestedCapability) {
    synchronized (this) {
      if (state == IOSServerManager.State.running) {
        return super.getNewSession(requestedCapability);
      } else {
        return null;
      }
    }

  }

}
