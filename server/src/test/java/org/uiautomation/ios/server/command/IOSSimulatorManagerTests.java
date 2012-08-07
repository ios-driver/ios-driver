package org.uiautomation.ios.server.command;

import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.instruments.SessionsManager;
import org.uiautomation.ios.server.tmp.SampleApps;

public class IOSSimulatorManagerTests {


  @Test(expectedExceptions={IOSAutomationSetupException.class})
  public void detectWrongSDKs() throws Exception {
    SessionsManager sessions = new SessionsManager(new IOSServerConfiguration());
    IOSCapabilities cap = IOSCapabilities.iphone(SampleApps.getUICatalogApp());
    cap.setSDKVersion("1234");
    sessions.createSession(cap);
  }
}
