package org.uiautomation.ios.server.application;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.utils.ClassicCommands;

import java.util.Locale;

public class RecognizeAllLanguagesInTheSafariAppTest {

  private APPIOSApplication app;

  @BeforeClass
  public void setup() throws Exception {

    String s = ClassicCommands.getDefaultSDK();
    app = APPIOSApplication.findSafariLocation(ClassicCommands.getXCodeInstall(), s);

  }
}
