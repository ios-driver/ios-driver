package org.uiautomation.ios.server.application;

import java.util.Locale;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class RecognizeAllLanguagesInTheSafariAppTest {

  private IOSApplication app;

  @BeforeClass
  public void setup() throws Exception {

    String s = ClassicCommands.getDefaultSDK();
    app = IOSApplication.findSafariLocation(ClassicCommands.getXCodeInstall(), s);

  }

  @Test
  public void noVariable() {
    //System.out.println(app.getSupportedLanguagesCodes());
    //for (Locale l : Locale.getAvailableLocales()){
    //  System.out.println(l.toString()+"\t"+l.getLanguage()+"\t"+l.getDisplayLanguage());
    //}*/
  }

}
