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

package org.uiautomation.ios.setup;

import org.libimobiledevice.ios.driver.binding.exceptions.SDKException;
import org.libimobiledevice.ios.driver.binding.model.ApplicationInfo;
import org.libimobiledevice.ios.driver.binding.services.AppContainerService;
import org.libimobiledevice.ios.driver.binding.services.DeviceService;
import org.libimobiledevice.ios.driver.binding.services.IOSDevice;
import org.libimobiledevice.ios.driver.binding.services.InformationService;
import org.libimobiledevice.ios.driver.binding.services.InstallCallback;
import org.libimobiledevice.ios.driver.binding.services.InstallerService;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.RealDevice;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.application.APPIOSApplication;
import org.uiautomation.ios.application.IOSRunningApplication;
import org.uiautomation.ios.application.IPAApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IOSRealDeviceManager implements IOSDeviceManager {

  private static final Logger log = Logger.getLogger(IOSRealDeviceManager.class.getName());
  private IOSDevice device;
  private final IOSRunningApplication app;
  private final IPAApplication ipa;
  private final ServerSideSession session;
  private final InformationService infoService;
  private final String bundleId;
  private final List<String> keysToConsiderInThePlistToHaveEquality;
  private final InstallerService installer;


  public IOSRealDeviceManager(ServerSideSession session) throws SDKException {
    this.session = session;
    app = session.getApplication();
    ipa = (IPAApplication) app.getUnderlyingApplication();
    RealDevice d = (RealDevice) session.getDevice();

    IOSDevice device = DeviceService.get(d.getUuid());
    this.device = device;
    installer = new InstallerService(device);
    bundleId = session.getCapabilities().getBundleId();

    keysToConsiderInThePlistToHaveEquality = new ArrayList<String>();
    keysToConsiderInThePlistToHaveEquality.add("CFBundleVersion");
    infoService = new InformationService(device);
  }

  @Override
  public void setup() {

    try {
      install(app.getUnderlyingApplication());
      setL10N(session.getCapabilities().getLocale(), session.getCapabilities().getLanguage());
    } catch (SDKException e) {
      throw new WebDriverException("error installing to device " + e.getMessage(), e);
    }
  }

  @Override
  public void teardown() {

  }

  private void install(APPIOSApplication aut) throws SDKException {
    if (aut.isSafari()) {
      return;
    }

    if (aut instanceof IPAApplication) {

      final InstallCallback cb = new InstallCallback() {
        @Override
        protected void onUpdate(String operation, int percent, String message) {
          sout(operation, percent, message);
        }
      };

      ApplicationInfo app = null;

      try {
        app = installer.getApplication(bundleId);
      } catch (Exception e) {
        //ignore.
      }
      // not installed ? install the app.
      if (app == null) {
        installer.install(((IPAApplication) aut).getIPAFile(), cb);
        return;
      }

      // already there and correct version
      if (isCorrectVersion(app)) {
        AppContainerService appContainerService = new AppContainerService((device));
        appContainerService.clean(aut.getBundleId());
        appContainerService.free();
        return;
      }

      // TODO upgrade ?
      // needs to re-install
      log.fine("uninstalling " + bundleId);
      installer.uninstall(bundleId);
      log.fine("installing " + bundleId);
      installer.install(((IPAApplication) aut).getIPAFile(), cb);
      log.fine(bundleId + " installed.");
    } else {
      throw new WebDriverException("only IPA apps can be used on a real device.");
    }
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
      Object exp = ipa.getMetadata().opt(key);
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

  private void setL10N(String locale, String language) throws SDKException {
    infoService.setLanguage(language);
    infoService.setLocale(locale);
  }
}
