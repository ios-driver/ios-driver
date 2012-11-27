package org.openqa.selenium.testing;

import java.io.File;

import org.openqa.selenium.WebDriverException;

public class InProject {
  /**
   * Locates a file in the current project
   * 
   * @param path path to file to locate from root of project
   * @return file being sought, if it exists
   * @throws org.openqa.selenium.WebDriverException wrapped FileNotFoundException if file could not
   *         be found
   */
  public static File locate(String path) {
    File dir = new File(".").getAbsoluteFile();
    dir = new File(dir,"src/test/resources/copyFromSeleniumProject");
    File needle = new File(dir,path);
   
    if (!needle.exists()){
      throw new WebDriverException("Cannot load "+needle);
    }
    return needle;
   }
}