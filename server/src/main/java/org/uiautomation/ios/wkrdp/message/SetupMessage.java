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

package org.uiautomation.ios.wkrdp.message;


public class SetupMessage extends BaseIOSWebKitMessage {

  public SetupMessage(String rawMessage) throws Exception {
    super(rawMessage);

  }


  @Override
  public String toString() {
    return "TODO";
  }
}


/*

<key>1</key>
  <dict>
          <key>WIRConnectionIdentifierKey</key>
          <string>41DC39AA-55A7-4C85-9566-B58E6627DD62</string>
          <key>WIRURLKey</key>
          <string>http://localhost:34024/common/alerts.html</string>
          <key>WIRTitleKey</key>
          <string>Testing Alerts</string>
          <key>WIRPageIdentifierKey</key>
          <integer>1</integer>
  </dict>
<key>2</key>
  <dict>
          <key>WIRPageIdentifierKey</key>
          <integer>2</integer>
          <key>WIRTitleKey</key>
          <string>blank</string>
          <key>WIRURLKey</key>
          <string>http://localhost:34024/common/blank.html</string>
  </dict>

*/