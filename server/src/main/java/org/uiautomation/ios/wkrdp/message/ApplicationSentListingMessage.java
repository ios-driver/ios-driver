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

/**
 * <code>ApplicationSentListingMessage</code> encapsulates information received through WebKitRemoteDebug protocol whose
 * 'selector' key has the string value '_rpc_applicationSentListing:'.
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
 * <plist version="1.0">
 * <dict>
 * <key>__argument</key>
 * <dict>
 *   <key>WIRApplicationIdentifierKey</key>
 *   <string>com.apple.mobilesafari</string>
 *   <key>WIRListingKey</key>
 *   <dict>
 *     <key>1</key>
 *     <dict>
 *       <key>WIRPageIdentifierKey</key>
 *       <integer>1</integer>
 *       <key>WIRTitleKey</key>
 *       <string></string>
 *       <key>WIRURLKey</key>
 *       <string>about:blank</string>
 *     </dict>
 *   </dict>
 * </dict>
 * <key>__selector</key>
 * <string>_rpc_applicationSentListing:</string>
 * </dict>
 * </plist>
 * }
 * </pre>
 */
public interface ApplicationSentListingMessage {

  static final String SELECTOR = "_rpc_applicationSentListing:";

  static final String WIRLISTINGKEY = "WIRListingKey";

  static final String WIRAPPLICATIONIDENTIFIERKEY = "WIRApplicationIdentifierKey";

  static final String WIRTYPEKEY = "WIRTypeKey";

  static final String WIRTYPEWEB = "WIRTypeWeb";

  /**
   * Returns true if the number pages received under the key "WIRListingKey" of an ApplicationSentListing message is
   * greater than zero
   * 
   * @return true if there are non zero pages , false otherwise.
   */
  boolean isPagesAvailable();

  /**
   * Returns the value of the key 'WIRApplicationIdentifierKey'
   * 
   * @return Application identifier
   */
  String getApplicationIdentifier();

  /**
   * Returns a {@link List} of {@link WebkitPage}s represented by the key 'WIRListingKey'
   * 
   * @return {@link List} of {@link WebkitPage}s
   */
  List<WebkitPage> getPages();

}
