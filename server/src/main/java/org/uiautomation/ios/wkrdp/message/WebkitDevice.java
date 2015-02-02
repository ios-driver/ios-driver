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
 * <code>WebkitDevice</code> class encapsulates information passed inside the key '__argument' of a
 * {@link ReportSetupMessage
 * <pre>
 * {@code
 * <dict>
 *   <key>WIRSimulatorBuildKey</key>
 *   <string>11D167</string>
 *   <key>WIRSimulatorProductVersionKey</key>
 *   <string>7.1</string>
 *   <key>WIRSimulatorNameKey</key>
 *   <string>iPhone Simulator</string>
 * </dict>
 * } 
 */
public interface WebkitDevice {

  static final String WIRSIMULATORBUILDKEY = "WIRSimulatorBuildKey";

  static final String WIRSIMULATORPRODUCTVERSIONKEY = "WIRSimulatorProductVersionKey";

  static final String WIRSIMULATORNAMEKEY = "WIRSimulatorNameKey";

  /**
   * Returns the simulator build ID represented by key 'WIRSimulatorBuildKey'
   * 
   * @return Simulator build ID.
   */
  String getSimulatorBuild();

  /**
   * Returns the simulator product version represented by the key 'WIRSimulatorProductVersionKey'
   * 
   * @return Simulator product version
   */
  String getSimulatorProductVersion();

  /**
   * Returns the simulator name represented by the key 'WIRSimulatorNameKey'
   * 
   * @return Simulator name
   */
  String getSimulatorName();

}
