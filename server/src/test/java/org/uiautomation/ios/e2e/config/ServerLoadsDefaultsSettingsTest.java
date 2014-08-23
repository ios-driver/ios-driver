package org.uiautomation.ios.e2e.config;

import junit.framework.Assert;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSServer;
import org.uiautomation.ios.IOSServerConfiguration;

public class ServerLoadsDefaultsSettingsTest {

  private IOSServer server;

  @BeforeClass
  public void setup() throws Exception {
    String[] args = {"-port", "4444", "-host", "localhost"};
    server = new IOSServer(args);
    server.start();
  }

  @Test
  public void serverLoadsDefaultTimeouts() throws Exception {
    IOSServerConfiguration config = server.getDriver().getIOSServerConfiguration();
    Assert.assertEquals(config.getNewSessionTimeoutSec(), IOSServerConfiguration.SESSION_START_TIME_OUT_SEC);
    Assert.assertEquals(config.getSessionTimeoutMillis(),IOSServerConfiguration.SESSION_TIME_OUT_SEC*1000);
    Assert.assertEquals(config.getMaxIdleTimeBetween2CommandsInSeconds(),IOSServerConfiguration.MAX_IDLE_TIME_BTWEEN_COMMAND_SEC);
  }

  @AfterClass
  public void teardown() throws Exception {
    if (server != null) {
      server.stop();
    }
  }



}
