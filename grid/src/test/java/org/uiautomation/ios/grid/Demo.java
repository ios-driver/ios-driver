package org.uiautomation.ios.grid;

import org.openqa.grid.selenium.GridLauncher;

public class Demo {

  public static void main(String[] args) throws Exception {
    String[] a = {"-port", "4444",
        //"-capabilityMatcher", "org.uiautomation.ios.grid.IOSCapabilityMatcher",
        "-role", "hub"};
    GridLauncher.main(a);
  }
}