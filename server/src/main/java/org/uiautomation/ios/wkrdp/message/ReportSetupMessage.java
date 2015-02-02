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
 * <code>ReportSetupMessage</code> encapsulates information received through WebKitRemoteDebug protocol whose
 * 'selector' key has the string value '_rpc_reportSetup:'.
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
 * <plist version="1.0">
 * <dict>
 * <key>__argument</key>
 * <dict>
 *   <key>WIRSimulatorBuildKey</key>
 *   <string>12B411</string>
 *   <key>WIRSimulatorProductVersionKey</key>
 *   <string>8.1</string>
 *   <key>WIRSimulatorNameKey</key>
 *   <string>iPhone Simulator</string>
 * </dict>
 * <key>__selector</key>
 * <string>_rpc_reportSetup:</string>
 * </dict>
 * </plist>
 * }
 * </pre>
 */
public interface ReportSetupMessage {

  static final String SELECTOR = "_rpc_reportSetup:";

  /**
   * Returns a {@link WebkitDevice} instance of the data represented by the key '__argument'
   * 
   * @return {@link WebkitDevice} instance
   */
  WebkitDevice getDevice();

}
