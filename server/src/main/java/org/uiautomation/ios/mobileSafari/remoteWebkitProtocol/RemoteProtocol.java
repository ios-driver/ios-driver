/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios.mobileSafari.remoteWebkitProtocol;

import org.uiautomation.ios.context.WebInspector;
import org.uiautomation.ios.mobileSafari.message.WebkitApplication;
import org.uiautomation.ios.mobileSafari.message.WebkitDevice;
import org.uiautomation.ios.mobileSafari.message.WebkitPage;

import java.util.ArrayList;
import java.util.List;

public class RemoteProtocol {

  static String safari = "com.apple.mobilesafari";
  static String uiCatalog = "com.yourcompany.UICatalog";
  private SimulatorSession simulator;

  private Object usbProtocol;

  private WebkitDevice device;
  private List<WebkitApplication> applications = new ArrayList<WebkitApplication>();


  public RemoteProtocol() {
    simulator = new SimulatorSession();
    usbProtocol = new Object();
  }

  public SimulatorSession getSimulator() {
    return simulator;
  }


  public WebInspector connect(String bundleId, String deviceId) {
    return null;
  }

  public void connect(String bundleId) {
    simulator.connect(bundleId);
  }


  public static void main(String[] args) throws Exception {

    RemoteProtocol protocol = new RemoteProtocol();
    SimulatorSession sim = protocol.getSimulator();
    sim.connect(uiCatalog);
    System.out.println(sim.getPages());
    WebkitPage p = sim.getPages().get(0);
    //WebkitPage ebay  =sim.getPages().get(1);
    WebInspector inspector1 = sim.connect(p);

    while (true) {
      Thread.sleep(5000);
      inspector1.test();
    }

    /*WebInspector inspector2 = sim.connect(ebay);
    inspector2.test();
    inspector1.test();*/
  }
}



