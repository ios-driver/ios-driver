package org.uiautomation.ios.server.simulator;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;

import java.io.File;

public class IOSRealDeviceManager implements IOSDeviceManager {

  public IOSRealDeviceManager(IOSCapabilities capabilities) {

  }

  @Override
  public void setL10N(String locale, String language) {
    // no-op
  }

  @Override
  public void resetContentAndSettings() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void cleanupDevice() {
    // no-op
  }

  @Override
  public void setKeyboardOptions() {
    // no-op
  }

  @Override
  public void setLocationPreference(boolean authorized) {
    // no-op
  }

  @Override
  public void setVariation(DeviceType device, DeviceVariation variation) {
    // no-op
  }

  @Override
  public String getInstrumentsClient() {
    return "instruments";
  }
}
