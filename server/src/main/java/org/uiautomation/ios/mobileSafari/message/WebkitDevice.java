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

package org.uiautomation.ios.mobileSafari.message;

import com.dd.plist.NSDictionary;

public class WebkitDevice {

  private final String WIRSimulatorNameKey;
  private final String WIRSimulatorBuildKey;

  public WebkitDevice(NSDictionary dict) {
    WIRSimulatorNameKey = dict.objectForKey("WIRSimulatorNameKey").toString();
    WIRSimulatorBuildKey = dict.objectForKey("WIRSimulatorBuildKey").toString();
  }


  @Override
  public String toString() {
    return WIRSimulatorNameKey + ",build=" + WIRSimulatorBuildKey;
  }

}

/*

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>__selector</key>
	<string>_rpc_reportSetup:</string>
	<key>__argument</key>
	<dict>
		<key>WIRSimulatorNameKey</key>
		<string>iPhone Simulator</string>
		<key>WIRSimulatorBuildKey</key>
		<string>10B5105c</string>
	</dict>
</dict>
</plist>


 */
