package org.openqa.selenium.testing;

import org.openqa.selenium.WebDriverException;

import java.io.File;

public class InProject {

  /**
   * Locates a file in the current project
   *
   * @param path path to file to locate from root of project
   * @return file being sought, if it exists
   * @throws WebDriverException wrapped FileNotFoundException if file could not be found
   */
  public static File locate(String path) {
    // TODO freynaud find why intelliJ is different from eclipse + maven.
    File dir = new File("ios-selenium-tests").getAbsoluteFile();
    dir = new File(dir, "src/test/resources/copyFromSeleniumProject");
    File needle = new File(dir, path);

    if (!needle.exists()) {
      dir = new File(".").getAbsoluteFile();
      dir = new File(dir, "src/test/resources/copyFromSeleniumProject");
      needle = new File(dir, path);
    }
    if (!needle.exists()) {
      throw new WebDriverException("Cannot load " + needle);
    }
    return needle;
  }
}