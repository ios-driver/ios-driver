package org.uiautomation.ios.server.simulator;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.RealDevice;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.application.IPAApplication;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;
import org.uiautomation.iosdriver.ApplicationInfo;
import org.uiautomation.iosdriver.services.DeviceInstallerService;
import org.uiautomation.iosdriver.services.DeviceManagerService;

public class IOSRealDeviceManager implements IOSDeviceManager {

  private final RealDevice device;
  private final IOSCapabilities capabilities;
  private final IPAApplication app;
  private final DeviceInstallerService service;
  private final String bundleId;

  public IOSRealDeviceManager(IOSCapabilities capabilities, RealDevice device, IPAApplication app) {
    this.device = device;
    bundleId = capabilities.getBundleId();
    this.capabilities = capabilities;
    this.app = app;
    this.service = new DeviceInstallerService(device.getUuid());
  }

  @Override
  public void install(APPIOSApplication aut) {
    if (aut instanceof IPAApplication) {
      String bundleId = capabilities.getBundleId();
      ApplicationInfo app = service.getApplication(bundleId);
      // not installed ? install the app.
      if (app == null) {
        service.install(((IPAApplication) aut).getIPAFile());
        return;
      }

      // already there and correct version
      if (isCorrectVersion(app)) {
        return;
      }

      // TODO upgrade ?
      // needs to re-install
      service.uninstall(bundleId);
      service.install(((IPAApplication) aut).getIPAFile());

    } else {
      throw new WebDriverException("only IPA apps can be used on a real device.");
    }
  }

  private boolean isCorrectVersion(ApplicationInfo app) {
    return true;
  }

  @Override
  public void setL10N(String locale, String language) {
    // no-op
  }

  @Override
  public void resetContentAndSettings() {
    // cannot access device while detecting.
    service.emptyApplicationCache("com.ebay.iphone");
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
