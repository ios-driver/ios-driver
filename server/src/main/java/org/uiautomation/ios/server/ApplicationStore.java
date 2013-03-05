package org.uiautomation.ios.server;

import org.uiautomation.ios.server.application.IOSApplication;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class ApplicationStore {

  private static final Logger log = Logger.getLogger(ApplicationStore.class.getName());
  private final List<IOSApplication> all = new CopyOnWriteArrayList<IOSApplication>();
  private final File workingDirectory;

  public ApplicationStore() {

    workingDirectory = new File("archived");
    workingDirectory.mkdir();
    log.info("Real device app archive :" + workingDirectory.getAbsolutePath());


  }

  public File getFolder() {
    return workingDirectory;
  }
}
