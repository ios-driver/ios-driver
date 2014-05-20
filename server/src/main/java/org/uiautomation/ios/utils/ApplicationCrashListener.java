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

package org.uiautomation.ios.utils;


import org.uiautomation.ios.ServerSideSession;

import java.util.logging.Logger;

public class ApplicationCrashListener implements CommandOutputListener {

  private static final Logger log = Logger.getLogger(ApplicationCrashListener.class.getName());
  private final ServerSideSession session;

  public ApplicationCrashListener(ServerSideSession session) {
    this.session = session;
  }

  @Override
  public void stdout(String log) {
    hasApplicationCrashed(log);
  }

  @Override
  public void stderr(String log) {
    hasApplicationCrashed(log);
  }

  private void hasApplicationCrashed(String log) {
    if (log.contains("The target application appears to have died") || log
        .contains("Script was stopped by the user")) {
      this.log.warning("log from crash " + log);
      session.sessionHasCrashed(log);
    }
  }
}
