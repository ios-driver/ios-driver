/*
 * Copyright 2012 ios-driver committers.
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

package org.uiautomation.ios.server.utils;

import java.io.File;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;



public class BuildInfo {

  private static final Properties BUILD_PROPERTIES = loadBuildProperties();

  private static Properties loadBuildProperties() {
    Properties properties = new Properties();

    try {
      Manifest manifest = null;
      URL url = BuildInfo.class.getProtectionDomain().getCodeSource().getLocation();
      File file = new File(url.toURI());
      JarFile jar = new JarFile(file);
      manifest = jar.getManifest();
      Attributes attributes = manifest.getMainAttributes();
      Set<Entry<Object, Object>> entries = attributes.entrySet();
      for (Entry<Object, Object> e : entries) {
        properties.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
      }
    } catch (Exception e) {
      //throw new InstantiationError("Cannot load info for jar manifest." + e.getMessage());
    }

    return properties;
  }

  public static String getAttribute(String key) {
    return BUILD_PROPERTIES.getProperty(key, "unknown");
  }
}
