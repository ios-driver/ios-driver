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
 * <code>WebkitApplication</code> class encapsulates information passed inside the element WIRApplicationDictionaryKey
 * of a {@link ReportConnectedApplicationsMessage} or as arguments of {@link ApplicationConnectedMessage}
 * 
 */
public interface WebkitApplication {

  public static final String WIRAPPLICATIONIDENTIFIERKEY = "WIRApplicationIdentifierKey";

  public static final String WIRAPPLICATIONNAMEKEY = "WIRApplicationNameKey";

  public static final String WIRISAPPLICATIONPROXYKEY = "WIRIsApplicationProxyKey";

  public static final String WIRAPPLICATIONBUNDLEIDENTIFIERKEY = "WIRApplicationBundleIdentifierKey";

  public static final String WIRISAPPLICATIONACTIVEKEY = "WIRIsApplicationActiveKey";

  public static final String SAFARI = "Safari";

  public static final String SAFARIBUNDLEIDENTIFIER = "com.apple.mobilesafari";

  public static final String WEBKITBUNDLEIDENTIFIER = "com.apple.WebKit.WebContent";

  /**
   * Returns the information represented by the key 'WIRApplicationIdentifierKey';
   * 
   * @return Application identifier
   */
  String getBundleId();

  /**
   * Returns the information represented by the key 'WIRApplicationNameKey'
   * 
   * @return Application name
   */
  String getApplicationName();

  /**
   * Returns true if the application is an instance of Safari
   * 
   * @return true if Safari, false otherwise.
   */
  boolean isSafari();

  /**
   * Returns true if this application can be connected by the WebKitRemoteDebug protocol. Implementation can override
   * the behavior for decision making capabilities of upper layers
   * 
   * @return true if allowed by WebKitRemoteDebug protocol, false otherwise
   */
  boolean isConnectableByWkrdProtocol();

}
