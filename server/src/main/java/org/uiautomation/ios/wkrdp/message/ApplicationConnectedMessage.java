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

/**
 * <code>ApplicationConnectedMessage</code> encapsulates information received through WebKitRemoteDebug protocol whose
 * 'selector' key has the string value '_rpc_applicationConnected:'.
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 *<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
 *<plist version="1.0">
 *<dict>
 * <key>__argument</key>
 * <dict>
 *   <key>WIRApplicationIdentifierKey</key>
 *   <string>PID:11460</string>
 *   <key>WIRIsApplicationProxyKey</key>
 *   <true/>
 *   <key>WIRApplicationNameKey</key>
 *   <string></string>
 *   <key>WIRApplicationBundleIdentifierKey</key>
 *   <string>com.apple.WebKit.WebContent</string>
 *   <key>WIRIsApplicationActiveKey</key>
 *   <integer>0</integer>
 *   <key>WIRHostApplicationIdentifierKey</key>
 *   <string>PID:11449</string>
 * </dict>
 * <key>__selector</key>
 * <string>_rpc_applicationConnected:</string>
 *</dict>
 *</plist>
 * }
 * </pre>
 */
public interface ApplicationConnectedMessage {

  static final String SELECTOR = "_rpc_applicationConnected:";

  /**
   * Returns a {@link WebkitApplication} instance of data represented the key '_argument'
   * @return {@link WebkitApplication}
   */
  WebkitApplication getApplication();

}
