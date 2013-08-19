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

package org.uiautomation.ios.server;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.application.IPAApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class ApplicationStore {

  private static final Logger log = Logger.getLogger(ApplicationStore.class.getName());
  private final List<APPIOSApplication> all = new CopyOnWriteArrayList<APPIOSApplication>();
  private final File workingDirectory;

  public ApplicationStore(String folder) {
    if (folder == null) {
      folder = "archived";
    }
    workingDirectory = new File(folder);
    workingDirectory.mkdir();
    log.info("App archive folder:" + workingDirectory.getAbsolutePath());
  }

  public void add(APPIOSApplication app) {
    all.add(app);
  }

  public void add(String path) {
    if (path.endsWith(".ipa")) {
      IPAApplication app = IPAApplication.createFrom(new File(path));
      all.add(app);
    } else if (path.endsWith(".app")) {
      all.add(new APPIOSApplication(path));
    }
  }

  public List<APPIOSApplication> getApplications() {
    return all;
  }

  public APPIOSApplication getApplication(String applicationPath) {
    for (APPIOSApplication a : all) {
      if (applicationPath.equals(a.getApplicationPath().getAbsolutePath())) {
        return a;
      }
    }
    return null;
  }

  public List<APPIOSApplication> getSimulatorApplications() {
    List<APPIOSApplication> res = new ArrayList<APPIOSApplication>();
    for (APPIOSApplication a : all) {
      if (!(a instanceof IPAApplication)) {
        res.add(a);
      }
    }
    return res;
  }

  public List<APPIOSApplication> getRealDeviceApplications() {
    List<APPIOSApplication> res = new ArrayList<APPIOSApplication>();
    for (APPIOSApplication a : all) {
      if (a instanceof IPAApplication) {
        res.add(a);
      }
    }
    return res;
  }

  public File getFolder() {
    return workingDirectory;
  }

  public String toString() {
    StringBuilder b = new StringBuilder();
    for (APPIOSApplication app : all) {
      b.append(app.toString());
    }
    return b.toString();
  }

  public List<IOSCapabilities> getCapabilities(Device d) {
    List<IOSCapabilities> res = new ArrayList<IOSCapabilities>();
    for (APPIOSApplication app : getApplications()) {
      if (d.canRun(app)) {
        IOSCapabilities c = merge(d.getCapability(), app.getCapabilities());
        res.add(c);
      }
    }
    return res;
  }

  private IOSCapabilities merge(IOSCapabilities device, IOSCapabilities application) {
    IOSCapabilities res = new IOSCapabilities();
    if (device.isSimulator()) {
      for (String key : device.asMap().keySet()) {
        res.setCapability(key, device.getCapability(key));
      }
      for (String key : application.asMap().keySet()) {
        res.setCapability(key, application.getCapability(key));
      }
    } else {
      for (String key : application.asMap().keySet()) {
        res.setCapability(key, application.getCapability(key));
      }
      for (String key : device.asMap().keySet()) {
        res.setCapability(key, device.getCapability(key));
      }
    }
    return res;
  }

  public File getArchiveFolder() {
    return workingDirectory;
  }

}
