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

package org.uiautomation.ios.wkrdp.message.ios7;

import org.uiautomation.ios.wkrdp.message.AbstractWebkitApplication;
import org.uiautomation.ios.wkrdp.message.WebkitApplication;

import com.dd.plist.NSDictionary;

/**
 * Implementation of {@link WebkitApplication} for IOS 7.
 * <pre>
 * {@code
 *       <dict>
 *           <key>WIRApplicationIdentifierKey</key>
 *           <string>com.apple.mobilesafari</string>
 *           <key>WIRConnectionIdentifierKey</key>
 *           <string>eabc44cc-2ba4-4635-96d2-463fe3154380</string>
 *           <key>WIRPageIdentifierKey</key>
 *           <integer>1</integer>
 *           <key>WIRSenderKey</key>
 *           <string>E0F4C128-F4FF-4D45-A538-BA382CD66001</string>
 *       </dict>
 * }
 */
public class WebkitApplicationImpl extends AbstractWebkitApplication {

  public WebkitApplicationImpl(NSDictionary dict) {
    super(dict);
  }

  @Override
  public boolean isConnectableByWkrdProtocol() {
    return true;
  }

  @Override
  public String toString() {
    return "[ " + WIRAPPLICATIONIDENTIFIERKEY + " = " + wirApplicationIdentifier + ", " + WIRAPPLICATIONNAMEKEY + " = "
        + wirApplicationName + " ]";
  }

  @Override
  protected boolean isSafariApp() {
    if (SAFARI.equals(wirApplicationName) && SAFARIBUNDLEIDENTIFIER.equals(wirApplicationIdentifier)) {
      return true;
    }
    return false;
  }

}
