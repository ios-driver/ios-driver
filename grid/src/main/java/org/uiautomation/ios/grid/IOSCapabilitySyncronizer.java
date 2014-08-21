package org.uiautomation.ios.grid;


import org.openqa.grid.internal.TestSlot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class IOSCapabilitySyncronizer {

  private boolean active;
  private IOSMutableRemoteProxy proxy;
  private URL node;
  private final List<DesiredCapabilities> registeredCapabilities;
  private int noResponse;
  private final StatusToCapabilitiesService service;
  private static final Logger log = Logger.getLogger(IOSCapabilitiesMonitor.class.getName());

  public IOSCapabilitySyncronizer(IOSMutableRemoteProxy proxy) {
    this.proxy = proxy;
    this.node = proxy.getRemoteHost();
    this.registeredCapabilities = proxy.getOriginalRegistrationRequest().getCapabilities();
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


  private void loop() {
    while (active) {
      try {
        org.openqa.grid.common.RegistrationRequest latest = createRegistrationRequest();
        if (latest == null) {
          noResponse++;
          //proxy.setAvailable(false);
          if (noResponse >= 30) {
            active = false;
            removeNode();
          }
          continue;
        }
        noResponse = 0;
        //proxy.setAvailable(true);
        List<DesiredCapabilities> latestCapabilities = latest.getCapabilities();

        if (!equals(latestCapabilities,registeredCapabilities)){
          updateCapabilities(latest);

        }
        /*if (!registeredCapabilities.toString().equals(latestCapabilities.toString())) {
          log.info("New capabilities registered on " + node.toString() + ". Updating...");
          log.info(registeredCapabilities.toString());
          log.info(latestCapabilities.toString());
          updateCapabilities(latest);
        }*/



        Thread.sleep(2000);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private boolean equals(List<DesiredCapabilities> latestCapabilities, List<DesiredCapabilities> registeredCapabilities) {
    if (latestCapabilities.size()!=registeredCapabilities.size()){
      return false;
    }
    for (int i=0;i<latestCapabilities.size();i++){
      Map<String,?> a = latestCapabilities.get(i).asMap();
      Map<String,?> b = registeredCapabilities.get(i).asMap();
      for (String key : a.keySet()){
        Object ao = a.get(key);
        Object bo = b.get(key);
        if (ao == null && bo!=null){
          log.info("\n\nDIFFERENT1"+key+"="+ao+"!="+bo);
          return false;
        }
        if (bo==null && ao!=null){
          log.info("\n\nDIFFERENT2"+key+"="+ao+"!="+bo);
          return false;
        }
        if (!ao.toString().equals(bo.toString())){
          log.info("\n\nDIFFERENT3"+key+"="+ao+"!="+bo);

          log.info(a.toString());
          log.info(b.toString());
          return false;
        }
      }
    }
    return true;
  }

  private org.openqa.grid.common.RegistrationRequest createRegistrationRequest() throws Exception {
    org.openqa.grid.common.RegistrationRequest registrationRequest = new org.openqa.grid.common.RegistrationRequest();

    List<DesiredCapabilities> capabilities = service.getNodeCapabilities();

    if (capabilities == null) {
      return null;
    }
    for (DesiredCapabilities cap : capabilities) {
      registrationRequest.addDesiredCapability(cap);
    }

    registrationRequest.getConfiguration().put(org.openqa.grid.common.RegistrationRequest.AUTO_REGISTER, true);
    registrationRequest.getConfiguration().put(org.openqa.grid.common.RegistrationRequest.PROXY_CLASS, proxy.getClass().getCanonicalName());

    registrationRequest.getConfiguration()
        .put(org.openqa.grid.common.RegistrationRequest.HUB_HOST, proxy.getRegistry().getHub().getHost());
    registrationRequest.getConfiguration()
        .put(org.openqa.grid.common.RegistrationRequest.HUB_PORT, proxy.getRegistry().getHub().getPort());
    registrationRequest.getConfiguration()
        .put(org.openqa.grid.common.RegistrationRequest.REMOTE_HOST, "http://" + node.getHost() + ":" + node.getPort());
    registrationRequest.getConfiguration().put(org.openqa.grid.common.RegistrationRequest.MAX_SESSION, 1);

    return registrationRequest;
  }


  private void removeNode() {
    //proxy.teardown();
    //proxy.unregister();
  }

  private void updateCapabilities(org.openqa.grid.common.RegistrationRequest registrationRequest) {
    try {
      //proxy.setRestarting(true);
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

  private void registerNode(org.openqa.grid.common.RegistrationRequest registrationRequest) {
    org.openqa.grid.internal.utils.SelfRegisteringRemote remote = new org.openqa.grid.internal.utils.SelfRegisteringRemote(registrationRequest);
    remote.startRegistrationProcess();
  }

  public void sync() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        loop();
      }
    }).start();
  }
}
