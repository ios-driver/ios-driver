package org.uiautomation.ios.server.configuration;

import org.openqa.selenium.WebDriverException;

public class Configuration {

  public static boolean BETA_FEATURE = false;

  public static boolean FORCE_IPAD = false;

  public static void off() {
    throw new WebDriverException("You need to enable beta feature.");
  }
}
