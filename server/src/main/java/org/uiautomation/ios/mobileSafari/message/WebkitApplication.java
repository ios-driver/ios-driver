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
import com.dd.plist.NSNumber;

public class WebkitApplication {


  private final String WIRApplicationIdentifierKey;
  private final String WIRApplicationNameKey;
  private final boolean WIRIsApplicationProxyKey;

  public WebkitApplication(NSDictionary dict) {

    WIRApplicationIdentifierKey = dict.objectForKey("WIRApplicationIdentifierKey").toString();
    WIRApplicationNameKey = dict.objectForKey("WIRApplicationNameKey").toString();
    NSNumber o = (NSNumber) dict.objectForKey("WIRIsApplicationProxyKey");
    WIRIsApplicationProxyKey = o.boolValue();
  }

  public String getBundleId() {
    return WIRApplicationIdentifierKey;
  }

  @Override
  public String toString() {
    return getBundleId();
  }
}

/*

  <key>com.apple.mobilesafari</key>
  <dict>
          <key>WIRApplicationIdentifierKey</key>
          <string>com.apple.mobilesafari</string>
          <key>WIRApplicationNameKey</key>
          <string>Safari</string>
          <key>WIRIsApplicationProxyKey</key>
          <false/>
  </dict>
*/