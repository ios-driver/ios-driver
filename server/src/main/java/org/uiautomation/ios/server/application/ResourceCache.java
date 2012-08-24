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

public class ResourceCache {

  List<Mapping> mapping = new ArrayList<ResourceCache.Mapping>();

  public void cacheResource(IOSApplication app) {
    for (String key : app.getResources().keySet()) {
      Mapping m = new Mapping(app, key, app.getResources().get(key));
      mapping.add(m);
    }
  }

  public File getResourceForKey(String key) {
    for (Mapping m : mapping) {
      if (key.equals(m.key)) {
        return new File(m.app.getApplicationPath(), m.resource);
      }
    }
    return null;
  }

  public String getKey(IOSApplication app, String name) {
    for (Mapping m : mapping) {
      if (app.equals(m.app) && m.name.equals(name)) {
        return m.key;
      }
    }
    return null;
  }

  class Mapping {
    private IOSApplication app;
    private String key;
    private String name;
    private String resource;


    public Mapping(IOSApplication app, String name, String resource) {
      this.name = name;
      this.app = app;
      this.resource = resource;
      this.key = "hash=" + resource.hashCode();
    }
  }



}
