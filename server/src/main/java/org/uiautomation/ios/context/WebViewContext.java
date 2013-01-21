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

package org.uiautomation.ios.context;

import org.uiautomation.ios.mobileSafari.SimulatorProtocolImpl;
import org.uiautomation.ios.mobileSafari.EventListener;
import org.uiautomation.ios.mobileSafari.ResponseFinder;
import org.uiautomation.ios.mobileSafari.remoteWebkitProtocol.MessageListener;

/**
 * Context for the webview accessed with the remote webkit protocol. If the application was launched
 * with instruments, and can be controled that way, it can be accessed via the ApplicationContext.
 * If application context is null, it means the WebContext was started on a non instrumented app,
 * and only web access via the remoteWebKit protocol can be done.
 */
public class WebViewContext {

  private final WebInspectorSet windowSet = new WebInspectorSet();
  private final SimulatorProtocolImpl protocol;
  private final String bundleId;

  public WebViewContext(String bundleId, MessageListener listener, ResponseFinder... finders)
      throws Exception {
    this.bundleId = bundleId;
    protocol = new SimulatorProtocolImpl(listener, finders);
  }
}


