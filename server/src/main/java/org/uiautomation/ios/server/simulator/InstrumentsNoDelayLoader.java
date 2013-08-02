package org.uiautomation.ios.server.simulator;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InstrumentsNoDelayLoader {

  private static InstrumentsNoDelayLoader instance;
  private File instruments;
  private File dir;


  public static InstrumentsNoDelayLoader getInstance() {
    if (instance == null) {
      instance = new InstrumentsNoDelayLoader();
    }
    return instance;
  }


  public File getInstruments() {
    return instruments;
  }

  private InstrumentsNoDelayLoader() {
    dir = new File(System.getProperty("java.io.tmpdir"));
    extractFromJar("instruments");
    extractFromJar("InstrumentsShim.dylib");
    extractFromJar("ScriptAgentShim.dylib");
    extractFromJar("SimShim.dylib");
    instruments = new File(dir, "instruments");
    instruments.setExecutable(true);

  }

  private File extractFromJar(String resource) {
    InputStream
        is =
        InstrumentsNoDelayLoader.class
            .getResourceAsStream("/instruments_no_delay/" + resource);

    if (is == null) {
      String msg = "Cannot find " + resource + ".";
      throw new WebDriverException(msg);
    }
    File f = null;
    try {
      f = new File(dir, resource);
      FileOutputStream os = new FileOutputStream(f);
      IOUtils.copy(is, os);
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(os);
    } catch (IOException e) {
      throw new RuntimeException("Cannot extract" + e.getMessage(), e);
    }
    return f;
  }
}
