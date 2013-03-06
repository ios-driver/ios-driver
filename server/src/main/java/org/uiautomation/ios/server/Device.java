package org.uiautomation.ios.server;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.application.IOSApplication;

public interface Device {

  IOSCapabilities getCapability();

  boolean canRun(IOSApplication app);
}
