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

package org.uiautomation.ios.context;


import org.json.JSONObject;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.wkrdp.internal.WebKitRemoteDebugProtocol;

import java.util.logging.Logger;

public class WebInspector extends BaseWebInspector {

  private static final Logger log = Logger.getLogger(WebInspector.class.getName());

  private final WebViewContext context;
  private final int pageIdentifierKey;
  private final static String senderBase = "E0F4C128-F4FF-4D45-A538-BA382CD660";
  private final String senderKey;
  private final String connectionKey;
  private final String bundleId;
  private final WebKitRemoteDebugProtocol inspector;

  public WebInspector(WebViewContext webViewContext, int pageIdentifierKey,
                      WebKitRemoteDebugProtocol inspector, String bundleId,
                      String connectionKey, ServerSideSession session) {
    super(session);
    this.bundleId = bundleId;
    this.connectionKey = connectionKey;
    this.context = webViewContext;
    this.inspector = inspector;
    this.pageIdentifierKey = pageIdentifierKey;
    this.senderKey = generateSenderString(pageIdentifierKey);
  }


  public JSONObject sendCommand(JSONObject command) {
    JSONObject
        res =
        inspector.sendWebkitCommand(command, pageIdentifierKey);
    return res;
  }

  public String getSenderKey() {
    return senderKey;
  }

  public int getPageIdentifier() {
    return pageIdentifierKey;
  }

  private String generateSenderString(int pageIdentifierKey) {
    if (pageIdentifierKey < 10) {
      return senderBase + "0" + pageIdentifierKey;
    } else {
      return senderBase + pageIdentifierKey;
    }

  }
}
