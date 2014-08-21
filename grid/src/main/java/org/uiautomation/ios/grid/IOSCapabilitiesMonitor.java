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

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

@Deprecated
public class IOSCapabilitiesMonitor implements Runnable {

  private boolean active;
  private IOSRemoteProxy proxy;
  private URL node;
  private List<DesiredCapabilities> registeredCapabilities;
  private int noResponse;
  private final StatusToCapabilitiesService service;
  private static final Logger log = Logger.getLogger(IOSCapabilitiesMonitor.class.getName());

  public IOSCapabilitiesMonitor(IOSRemoteProxy proxy) {
    this.proxy = proxy;
    this.node = proxy.getRemoteHost();
    registeredCapabilities = proxy.getOriginalRegistrationRequest().getCapabilities();
    active = true;
    this.noResponse = 0;
    try {
      URL url = new URL("http://" + node.getHost() + ":" + node.getPort() + "/wd/hub/status");
      service = new StatusToCapabilitiesService(url);
    } catch (MalformedURLException e) {
      log.warning("wrong url for the status call " + e.getMessage());
      throw new WebDriverException(e);
    }
  }


  @Override
  public void run() {
    while (active) {
      try {
        RegistrationRequest latest = createRegistrationRequest();
        if (latest == null) {
          noResponse++;
          proxy.setAvailable(false);
          if (noResponse >= 30) {
            active = false;
            removeNode();
          }
          continue;
        }
        noResponse = 0;
        proxy.setAvailable(true);
        List<DesiredCapabilities> latestCapabilities = latest.getCapabilities();
        if (!registeredCapabilities.toString().equals(latestCapabilities.toString())) {
          log.info("New capabilities registered on " + node.toString() + ". Updating...");
          updateCapabilities(latest);
        }
        Thread.sleep(2000);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private RegistrationRequest createRegistrationRequest() throws Exception {
    RegistrationRequest registrationRequest = new RegistrationRequest();

    List<DesiredCapabilities> capabilities = service.getNodeCapabilities();

    if (capabilities == null) {
      return null;
    }
    for (DesiredCapabilities cap : capabilities) {
      registrationRequest.addDesiredCapability(cap);
    }

    registrationRequest.getConfiguration().put(RegistrationRequest.AUTO_REGISTER, true);
    registrationRequest.getConfiguration().put(RegistrationRequest.PROXY_CLASS,this.getClass().getCanonicalName());

    registrationRequest.getConfiguration()
        .put(RegistrationRequest.HUB_HOST, proxy.getRegistry().getHub().getHost());
    registrationRequest.getConfiguration()
        .put(RegistrationRequest.HUB_PORT, proxy.getRegistry().getHub().getPort());
    registrationRequest.getConfiguration()
        .put(RegistrationRequest.REMOTE_HOST, "http://" + node.getHost() + ":" + node.getPort());
    registrationRequest.getConfiguration().put(RegistrationRequest.MAX_SESSION, 1);

    return registrationRequest;
  }


  private void removeNode() {
    //proxy.teardown();
    proxy.unregister();
  }

  private void updateCapabilities(RegistrationRequest registrationRequest) {
    try {
      proxy.setRestarting(true);
      while (proxyBusy()) {
        System.out.println("Node busy... waiting...");
        Thread.sleep(1000);
      }
      active = false;
      registerNode(registrationRequest);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean proxyBusy() {
    List<TestSlot> slots = proxy.getTestSlots();
    for (TestSlot slot : slots) {
      if (slot.getSession() != null) {
        return true;
      }
    }
    return false;
  }

  private void registerNode(RegistrationRequest registrationRequest) {
    SelfRegisteringRemote remote = new SelfRegisteringRemote(registrationRequest);
    remote.startRegistrationProcess();
  }
}