package org.uiautomation.ios.server.simulator;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.RealDevice;
import org.uiautomation.ios.server.application.IPAApplication;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;
import org.uiautomation.iosdriver.services.DeviceInstallerService;

public class IOSRealDeviceManager implements IOSDeviceManager {

  private final RealDevice device;
  private final IOSCapabilities capabilities;
  private final IPAApplication app;

  public IOSRealDeviceManager(IOSCapabilities capabilities, RealDevice device, IPAApplication app) {
    this.device = device;
    this.capabilities = capabilities;
    this.app = app;
  }

  @Override
  public void setL10N(String locale, String language) {
    // no-op
  }

  @Override
  public void resetContentAndSettings() {
    DeviceInstallerService service = new DeviceInstallerService(device.getUuid());
    service.uninstall(app.getBundleId());
    service.install(app.getIPAFile());
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
  public void setMobileSafariOptions() {
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
