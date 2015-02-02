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

import org.uiautomation.ios.wkrdp.message.AbstractReportConnectedApplicationsMessage;
import org.uiautomation.ios.wkrdp.message.ReportConnectedApplicationsMessage;

import com.dd.plist.NSDictionary;

/**
 * Implementation of {@link ReportConnectedApplicationsMessage} for IOS7
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
 *<plist version="1.0">
 *<dict>
 * <key>__argument</key>
 * <dict>
 *   <key>WIRApplicationDictionaryKey</key>
 *   <dict>
 *     <key>com.apple.mobilesafari</key>
 *     <dict>
 *       <key>WIRApplicationNameKey</key>
 *       <string>Safari</string>
 *       <key>WIRIsApplicationProxyKey</key>
 *       <false/>
 *       <key>WIRApplicationIdentifierKey</key>
 *       <string>com.apple.mobilesafari</string>
 *     </dict>
 *   </dict>
 * </dict>
 * <key>__selector</key>
 * <string>_rpc_reportConnectedApplicationList:</string>
 *</dict>
 *</plist>
 * }
 */
public class ReportConnectedApplicationsMessageImpl extends AbstractReportConnectedApplicationsMessage {

  public ReportConnectedApplicationsMessageImpl(String rawMessage) throws Exception {
    super(rawMessage);
  }

  @Override
  protected void populateApplications() {
    NSDictionary list = (NSDictionary) arguments.objectForKey(WIRAPPLICATIONDICTIONARYKEY);
    String[] keys = list.allKeys();
    for (String key : keys) {
      NSDictionary app = (NSDictionary) list.objectForKey(key);
      WebkitApplicationImpl application = new WebkitApplicationImpl(app);
      applications.add(application);
    }
  }

}
