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
import com.dd.plist.NSObject;
import com.dd.plist.XMLPropertyListParser;

import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;

import java.util.List;


public class SetupMessage extends BaseIOSWebKitMessage {

  public SetupMessage(String rawMessage) throws Exception {
    super(rawMessage);

  }


  @Override
  public String toString() {
    return "TODO";
  }

  public void test() {
    /*List<DefaultElement> windows = document.selectNodes("/plist/dict/dict/dict/dict");
    try {
      NSDictionary rootDict = (NSDictionary) XMLPropertyListParser.parse(rawMessage.getBytes());
      String selector = rootDict.objectForKey("__selector").toString();

      NSDictionary arguments = (NSDictionary) rootDict.objectForKey("__argument");
      String app = arguments.objectForKey("WIRApplicationIdentifierKey").toString();
      System.out.println(selector + " app " + app);

      NSDictionary list = (NSDictionary) arguments.objectForKey("WIRListingKey");
      String[] keys = list.allKeys();

      for (String key : keys) {
        NSDictionary page = (NSDictionary) list.objectForKey(key);
        String id = page.objectForKey("WIRPageIdentifierKey").toString();
        String url = page.objectForKey("WIRURLKey").toString();
        String title = page.objectForKey("WIRTitleKey").toString();

        NSObject cnx = page.objectForKey("WIRConnectionIdentifierKey");
        String c = cnx == null ? null : cnx.toString();
        //System.out.println(id + ", " + url + " , " + title + "state:" + c);

      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(windows.size());
    for (DefaultElement win : windows) {

    } */
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