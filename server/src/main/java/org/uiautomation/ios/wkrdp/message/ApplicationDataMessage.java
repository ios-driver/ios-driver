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

import org.json.JSONObject;

/**
 * <code>ApplicationDataMessage</code> encapsulates information received through WebKitRemoteDebug protocol whose
 * 'selector' key has the string value '_rpc_applicationSentData:'.
 * <pre>
 * {@code
 *<?xml version="1.0" encoding="UTF-8"?>
 *<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
 *<plist version="1.0">
 *<dict>
 * <key>__argument</key>
 * <dict>
 *   <key>WIRDestinationKey</key>
 *   <string>E0F4C128-F4FF-4D45-A538-BA382CD66001</string>
 *   <key>WIRMessageDataKey</key>
 *   <data>
 *     eyJyZXN1bHQiOnt9LCJpZCI6Mn0=
 *   </data>
 *   <key>WIRApplicationIdentifierKey</key>
 *   <string>com.apple.mobilesafari</string>
 * </dict>
 * <key>__selector</key>
 * <string>_rpc_applicationSentData:</string>
 *</dict>
 *</plist>
 * }
 * </pre>
 */
public interface ApplicationDataMessage {

  static final String SELECTOR = "_rpc_applicationSentData:";

  static final String WIRDDESTINATIONKEY = "WIRDestinationKey";

  static final String WIRMESSAGEDATAKEY = "WIRMessageDataKey";

  static final String WIRAPPPLICATIONIDENTIFIERKEY = "WIRApplicationIdentifierKey";

  /**
   * Returns a {@link JSONObject} instance of the data represented by the key 'WIRMessageDataKey' after decoding the
   * data.
   * 
   * @return {@link JSONObject} instance of the data.
   */
  JSONObject getMessage();

  /**
   * Returns the string represented by the key 'WIRDestinationKey'
   * 
   * @return Destination key.
   */
  String getDestinationKey();

}
