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

import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

import org.json.JSONObject;
import org.libimobiledevice.binding.raw.ApplicationInfo;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.util.Map;

public class PlistFileUtils {

  private final File source;
  private final Map<String, Object> plistContent;

  public PlistFileUtils(File source) {
    this.source = source;
    this.plistContent = read(source);
  }


  private Map<String, Object> read(File plist) {
    try {
      NSObject object = PropertyListParser.parse(plist);
      ApplicationInfo info = new ApplicationInfo(object);
      return info.getProperties();
    } catch (Exception ex) {
      throw new WebDriverException(
          "Cannot parse " + plist.getAbsolutePath() + ": " + ex.getMessage(), ex);
    }
  }

  public JSONObject toJSON() throws Exception {
    JSONObject res = new JSONObject(plistContent);
    return res;
  }
}
