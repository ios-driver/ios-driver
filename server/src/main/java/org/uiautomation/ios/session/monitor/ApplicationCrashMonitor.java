/*
 * Copyright 2012-2014 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.session.monitor;


import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.utils.CommandOutputListener;

import java.util.logging.Logger;

public class ApplicationCrashMonitor implements ServerSideSessionMonitor, CommandOutputListener {

  private final ServerSideSession session;
  private static final Logger log = Logger.getLogger(ApplicationCrashMonitor.class.getName());
  private final String APP_DIED = "The target application appears to have died";
  private final String SCRIPT_ERROR = "An error occurred while trying to run the script";
  private final String STOP_BY_USER = "Script was stopped by the user";


  public ApplicationCrashMonitor(ServerSideSession session) {
    this.session = session;
  }

  @Override
  public void stdout(String output) {
    hasApplicationCrashed(output);
  }

  @Override
  public void stderr(String output) {
    hasApplicationCrashed(output);
  }

  private void hasApplicationCrashed(String output) {
    if (output.contains(APP_DIED) || output.contains(STOP_BY_USER) || output.contains(SCRIPT_ERROR)) {
      log.warning("log from crash " + output);
      session.stop(ServerSideSession.StopCause.crash);
    }
  }

  @Override
  public void startMonitoring() {
      session.getDualDriver().getNativeDriver().getInstruments().registerOutputListener(this);
  }

  @Override
  public void stopMonitoring() {

  }
}
