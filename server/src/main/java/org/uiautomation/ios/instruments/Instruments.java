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

package org.uiautomation.ios.instruments;

import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.command.UIAScriptRequest;
import org.uiautomation.ios.instruments.commandExecutor.UIAutomationCommandExecutor;
import org.uiautomation.ios.session.monitor.ApplicationCrashMonitor;
import org.uiautomation.ios.utils.CommandOutputListener;

public interface Instruments {

  void start(long timeOut) throws InstrumentsFailedToStartException, ApplicationCrashedOnStartException;
  int waitForProcessToDie();
  void stop();

  Response executeCommand(UIAScriptRequest request);

  UIAutomationCommandExecutor getChannel();

  TakeScreenshotService getScreenshotService();

  boolean isCompatible(ServerSideSession session);

  void registerOutputListener(CommandOutputListener listener);
}
