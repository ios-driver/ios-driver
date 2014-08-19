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

import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultCommandListener implements CommandOutputListener {

  private static final Logger log = Logger.getLogger(DefaultCommandListener.class.getName());
  private final String label;

  public DefaultCommandListener(String label){
    if (label != null && label.length() > 40)
        label = label.substring(0, 40);
    this.label=label;
  }

  @Override
  public void stdout(String line) {
    if (log.isLoggable(Level.FINE))
      log.warning(label+"\t"+line);
  }

  @Override
  public void stderr(String line) {
    log.warning(label+"\t"+line);
  }

}
