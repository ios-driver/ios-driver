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

package org.uiautomation.ios.utils;


import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.List;

public class ShowSDKsParser implements CommandOutputListener {

  private static final String pattern = "iphonesimulator";

  private List<String> sdks = new ArrayList<>();
  private boolean ok = true;
  private boolean needToFirstLaunchXcode = false;

  public void stdout(String log) {
    String sdk = extractSDK(log);
    if (sdk != null) {
      sdks.add(sdk);
    } else if (log.contains("Agreeing to the Xcode/iOS license requires admin privileges")) {
      needToFirstLaunchXcode = true;
    }
  }

  private String extractSDK(String log) {
    if (log.contains(pattern)) {
      int index = log.indexOf(pattern) + pattern.length();
      String raw = log.substring(index);
      return decorate(raw);
    } else {
      return null;
    }
  }

  private String decorate(String raw) {
    if (raw.equals("7.0")) {
      return "7.0.3";
    }
    return raw;
  }

  public void stderr(String log) {
    ok = false;
  }

  public List<String> getSDKs() {
    if (!ok && sdks.size() == 0) {
      throw new WebDriverException("there was an error.stderr is not empty");
    }
    return sdks;
  }

  public boolean isNeedToFirstLaunchXcode() {
    return needToFirstLaunchXcode;
  }
}
