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

package org.uiautomation.ios.instruments.commandExecutor;

import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.command.UIAScriptRequest;
import org.uiautomation.ios.command.UIAScriptResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiUIAutomationCommandExecutor extends BaseUIAutomationCommandExecutor {

  // private final InstrumentsService instruments;
  private static final Logger log = Logger
      .getLogger(MultiUIAutomationCommandExecutor.class.getName());

  public MultiUIAutomationCommandExecutor(int port, String aut, ServerSideSession session
                                                   /*InstrumentsService instruments*/) {
    super(session);
  }

  @Override
  public UIAScriptResponse executeCommand(UIAScriptRequest request) {
    handleLastCommand(request);
    sendNextCommand(request);
    UIAScriptResponse response = waitForResponse();
    return response;
  }



  private void sendNextCommand(UIAScriptRequest r) {
    try {
      String
          templ =
          "UIATarget.localTarget().frontMostApp().setPreferencesValueForKey( '%s', 'cmd');";

      String escaped = r.getScript().replace("'", "\"");
      String script = String.format(templ, escaped);
      //instruments.executeScriptNonManaged(script);
    } catch (Exception e) {
      log.log(Level.SEVERE,"TODO",e);
    }
  }

  public void handle(String message) {
    if (message.startsWith("IOS_DRIVER_RESPONSE:")) {

      String raw = message.replace("IOS_DRIVER_RESPONSE:", "");
      UIAScriptResponse response = new UIAScriptResponse(raw);
      if (response.isFirstResponse()) {
        registerUIAScript();
      } else {
        setNextResponse(response);
      }
    } else {
      log(message);
    }
  }

  public void log(String message) {
    System.out.println(message);
  }
}
