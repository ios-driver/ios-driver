package org.uiautomation.ios.server.command;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
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
