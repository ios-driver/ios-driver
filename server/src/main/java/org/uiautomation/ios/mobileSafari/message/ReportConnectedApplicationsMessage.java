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

import java.util.ArrayList;
import java.util.List;

public class ReportConnectedApplicationsMessage extends BaseIOSWebKitMessage {


  private final List<WebkitApplication> apps = new ArrayList<WebkitApplication>();

  public ReportConnectedApplicationsMessage(String rawMessage) throws Exception {
    super(rawMessage);
    NSDictionary list = (NSDictionary) arguments.objectForKey("WIRApplicationDictionaryKey");
    String[] keys = list.allKeys();

    for (String key : keys) {
      NSDictionary app = (NSDictionary) list.objectForKey(key);
      WebkitApplication application = new WebkitApplication(app);
      apps.add(application);
    }
  }

  public List<WebkitApplication> getApplications() {
    return apps;
  }


  @Override
  protected String toString(NSDictionary args) {
    return apps.toString();
  }
}
 /*

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>__selector</key>
	<string>_rpc_reportConnectedApplicationList:</string>
	<key>__argument</key>
	<dict>
		<key>WIRApplicationDictionaryKey</key>
		<dict>
			<key>com.apple.mobilesafari</key>
			<dict>
				<key>WIRApplicationIdentifierKey</key>
				<string>com.apple.mobilesafari</string>
				<key>WIRApplicationNameKey</key>
				<string>Safari</string>
				<key>WIRIsApplicationProxyKey</key>
				<false/>
			</dict>
		</dict>
	</dict>
</dict>
</plist>


  */