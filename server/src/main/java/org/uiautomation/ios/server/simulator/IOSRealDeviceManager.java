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

package org.uiautomation.ios.server.simulator;

import org.libimobiledevice.ios.driver.binding.model.ApplicationInfo;
import org.libimobiledevice.ios.driver.binding.exceptions.LibImobileException;
import org.libimobiledevice.ios.driver.binding.exceptions.SDKException;
import org.libimobiledevice.ios.driver.binding.services.DeviceService;
import org.libimobiledevice.ios.driver.binding.services.IOSDevice;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.RealDevice;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.application.IPAApplication;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IOSRealDeviceManager implements IOSDeviceManager {

  private static final Logger log = Logger.getLogger(IOSRealDeviceManager.class.getName());
  //private final RealDevice device;
  private final IOSCapabilities capabilities;
  private final IPAApplication app;
  private IOSDevice device;
  //private final DeviceInstallerService service;
  private final String bundleId;
  private final List<String> keysToConsiderInThePlistToHaveEquality;

  /**
   * @param capabilities the capability requested by the user.
   * @param device       the device that will host the test
   * @param app          the app that will be ran.
   */
  public IOSRealDeviceManager(IOSCapabilities capabilities, RealDevice device, IPAApplication app) {
    try {
      this.device = DeviceService.get(device.getUuid());
    } catch (SDKException e) {
      e.printStackTrace();
    } catch (LibImobileException e) {
      e.printStackTrace();
    }
    bundleId = capabilities.getBundleId();
    this.capabilities = capabilities;
    this.app = app;

    keysToConsiderInThePlistToHaveEquality = new ArrayList<String>();
    keysToConsiderInThePlistToHaveEquality.add("CFBundleVersion");
  }

  @Override
  public void install(APPIOSApplication aut) {
//    if (aut instanceof IPAApplication) {
//      ApplicationInfo app = device.getApplication(bundleId);
//      // not installed ? install the app.
//      if (app == null) {
//        device.install(((IPAApplication) aut).getIPAFile());
//        return;
//      }
//
//      // already there and correct version
//      if (isCorrectVersion(app)) {
//        return;
//      }
//
//      // TODO upgrade ?
//      // needs to re-install
//      log.fine("uninstalling " + bundleId );
//      device.uninstall(bundleId);
//      log.fine("installing " + bundleId );
//      device.install(((IPAApplication) aut).getIPAFile());
//      log.fine(bundleId + " installed.");
//    } else {
//      throw new WebDriverException("only IPA apps can be used on a real device.");
//    }
  }

  /**
   * validate the the application with the same bundleId on the device has the correct version, and
   * return true if it does. It currently only checks the the app is "the same" by checking the
   * CFBundleVersion. It should check more than that. Maybe all the magic capabilities ?
   */
  // TODO freynaud should be more strict than that and check all magic keys.
  private boolean isCorrectVersion(ApplicationInfo app) {
    for (String key : keysToConsiderInThePlistToHaveEquality) {
      Object value = app.getProperty(key);
      Object exp = this.app.getMetadata().opt(key);
      if (value instanceof String) {
        if (!value.equals(exp)) {
          log.fine("for key " + key + ",device has" + value + ",but needed " + exp
                   + ".the app will need to be reinstalled.");
          return false;
        }
      }
    }
    return true;
  }


  @Override
  public void setL10N(String locale, String language) {
//    device.setLockDownValue("com.apple.international", "Language", language);
//    device.setLockDownValue("com.apple.international", "Locale", locale);
  }

  @Override
  public void resetContentAndSettings() {
    //device.emptyApplicationCache(bundleId);
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
  public void setSDKVersion() {
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
  public String getDeviceSpecification(DeviceType device, DeviceVariation variation) {
    // not-applicable
    return null;
  }

  @Override
  public void setSimulatorScale(String scale) {
    // not-applicable
  }

  @Override
  public void installTrustStore(String trustStore) {
   // not-applicable
  }
}
