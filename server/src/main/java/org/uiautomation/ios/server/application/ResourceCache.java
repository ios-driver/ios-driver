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

package org.uiautomation.ios.server.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceCache {

  List<Mapping> mapping = new ArrayList<ResourceCache.Mapping>();

  public void cacheResource(String appPath) {
    Mapping m = new Mapping(appPath);
    mapping.add(m);
  }

  public File getResourceForKey(String key) {
    for (Mapping m : mapping) {
      if (key.equals(m.key)) {
        return m.resource;
      }
    }
    return null;
  }

  public String getKeyForApplication(String applicationPath) {
    for (Mapping m : mapping) {
      if (applicationPath.equals(m.applicationPath)) {
        return m.key;
      }
    }
    return null;
  }

  class Mapping {
    private String applicationPath;
    private String key;
    private File resource;

    public Mapping(String applicationPath) {
      this.applicationPath = applicationPath;
      String icon = new IOSApplication(null, applicationPath).getIcon();
      this.resource = new File(applicationPath, icon);
      this.key = UUID.randomUUID().toString();
    }
  }

}
