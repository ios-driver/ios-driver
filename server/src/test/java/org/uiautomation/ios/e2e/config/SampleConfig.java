package org.uiautomation.ios.e2e.config;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;

public class SampleConfig {

  private static final String SDK_VERSION_SYSTEM_PROPERTY = "SDK";
  private static final String UI_CATALOG_BUNDLE_NAME = "UICatalog";
  private static final String BUNDLE_VERSION = "2.10";

  public static IOSCapabilities uiCatalogCapNoLangNoLocale() {
    IOSCapabilities capabilities = new IOSCapabilities();
    capabilities.setCapability(IOSCapabilities.DEVICE, DeviceType.iphone);
    capabilities.setCapability(IOSCapabilities.BUNDLE_NAME, UI_CATALOG_BUNDLE_NAME);
    capabilities.setCapability(IOSCapabilities.BUNDLE_VERSION, BUNDLE_VERSION);
    String sdkVersion = System.getProperty(SDK_VERSION_SYSTEM_PROPERTY, null);
    if (sdkVersion != null) {
      capabilities.setSDKVersion(sdkVersion);
    }
    return capabilities;
  }
}
