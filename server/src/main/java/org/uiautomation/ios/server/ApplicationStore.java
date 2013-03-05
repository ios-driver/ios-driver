package org.uiautomation.ios.server;

import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.IPAApplication;

import java.io.File;
import java.util.ArrayList;
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
    load();
  }

  public void add(IOSApplication app) {
    all.add(app);
  }


  public void add(String path) {
    if (path.endsWith(".ipa")) {
      IPAApplication app = IPAApplication.createFrom(new File(path));
      all.add(app);
    } else if (path.endsWith(".app")) {
      all.add(new IOSApplication(path));
    }
  }

  public void load() {
    for (File f : workingDirectory.listFiles()) {
      add(f.getAbsolutePath());
    }
  }

  public List<IOSApplication> getApplications() {
    return all;
  }

  public List<IOSApplication> getSimulatorApplications() {
    List<IOSApplication> res = new ArrayList<IOSApplication>();
    for (IOSApplication a : all) {
      if (!(a instanceof IPAApplication)) {
        res.add(a);
      }
    }
    return res;
  }

  public List<IOSApplication> getRealDeviceApplications() {
    List<IOSApplication> res = new ArrayList<IOSApplication>();
    for (IOSApplication a : all) {
      if (a instanceof IPAApplication) {
        res.add(a);
      }
    }
    return res;
  }

  public File getFolder() {
    return workingDirectory;
  }

  public String toString() {
    StringBuilder b = new StringBuilder();
    for (IOSApplication app : all) {
      b.append(app.toString());
    }
    return b.toString();
  }


}
