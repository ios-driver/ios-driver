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
