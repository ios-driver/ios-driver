/*
 * Copyright 2012-2015 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.wkrdp.message.ios8;

import org.uiautomation.ios.wkrdp.message.AbstractWebkitApplication;
import org.uiautomation.ios.wkrdp.message.WebkitApplication;

import com.dd.plist.NSDictionary;

/**
 * Implementation of {@link WebkitApplication} for IOS 8.
 * <pre>
 * {@code
 * <dict>
 *   <key>WIRApplicationDictionaryKey</key>
 *   <dict>
 *     <key>PID:7286</key>
 *     <dict>
 *       <key>WIRApplicationIdentifierKey</key>
 *       <string>PID:7286</string>
 *       <key>WIRIsApplicationProxyKey</key>
 *       <false/>
 *       <key>WIRApplicationNameKey</key>
 *       <string>Safari</string>
 *       <key>WIRApplicationBundleIdentifierKey</key>
 *       <string>com.apple.mobilesafari</string>
 *       <key>WIRIsApplicationActiveKey</key>
 *       <integer>1</integer>
 *     </dict>
 *     <key>PID:7297</key>
 *     <dict>
 *       <key>WIRApplicationIdentifierKey</key>
 *       <string>PID:7297</string>
 *       <key>WIRIsApplicationProxyKey</key>
 *       <true/>
 *       <key>WIRApplicationNameKey</key>
 *       <string></string>
 *       <key>WIRApplicationBundleIdentifierKey</key>
 *       <string>com.apple.WebKit.WebContent</string>
 *       <key>WIRIsApplicationActiveKey</key>
 *       <integer>0</integer>
 *       <key>WIRHostApplicationIdentifierKey</key>
 *       <string>PID:7286</string>
 *     </dict>
 *   </dict>
 * </dict>
 * }
 */
public class WebkitApplicationImpl extends AbstractWebkitApplication {

  protected String wirApplicationBundleIdentifier;

  public WebkitApplicationImpl(NSDictionary nSDictionary) {
    super(nSDictionary);
    wirApplicationBundleIdentifier = nSDictionary.objectForKey(WIRAPPLICATIONBUNDLEIDENTIFIERKEY).toString();
  }

  @Override
  protected boolean isSafariApp() {
    if (SAFARI.equals(wirApplicationName) && SAFARIBUNDLEIDENTIFIER.equals(wirApplicationBundleIdentifier)) {
      return true;
    }
    return false;
  }

  public String getBundleId() {
    return wirApplicationBundleIdentifier;
  }

  public boolean isProxyApplication() {
    return wirIsApplicationProxy;
  }

  public boolean isWebKitWebContentApplication() {
    return WEBKITBUNDLEIDENTIFIER.equals(wirApplicationBundleIdentifier);
  }

  @Override
  public String toString() {
    return "[ " + WIRAPPLICATIONIDENTIFIERKEY + " = " + wirApplicationIdentifier + ", "
        + WIRAPPLICATIONBUNDLEIDENTIFIERKEY + " = " + wirApplicationBundleIdentifier + ", " + WIRAPPLICATIONNAMEKEY
        + " = " + wirApplicationName + " ]";
  }

}