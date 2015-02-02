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

import java.util.List;

import org.openqa.selenium.WebDriverException;

/**
 * <code>ReportConnectedApplicationsMessage</code> encapsulates information received through WebKitRemoteDebug protocol whose
 * 'selector' key has the string value '_rpc_reportConnectedApplicationList:'.
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
 * <plist version="1.0">
 * <dict>
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
 * </dict>
 * </plist>
 * }
 * </pre>
 */
public interface ReportConnectedApplicationsMessage {

  static final String SELECTOR = "_rpc_reportConnectedApplicationList:";

  static final String WIRAPPLICATIONDICTIONARYKEY = "WIRApplicationDictionaryKey";

  /**
   * Returns a {@link List} of {@link WebkitApplication} represented by the key 'WIRApplicationDictionaryKey'
   * 
   * @return {@link List} of {@link WebkitApplication}
   */
  List<WebkitApplication> getApplications();

  /**
   * Returns the {@link WebkitApplication} for the given applications identifier.
   * 
   * @param applicationIdentifier
   *          Application identifier of the sought application
   * @return {@link WebkitApplication} of the provided application identifier
   * @throws WebDriverException
   *           if {@link WebkitApplication} is not found
   */
  WebkitApplication getApplication(String applicationIdentifier);

}
