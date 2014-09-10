/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uiautomation.ios.utils;

import org.uiautomation.ios.IOSServerConfiguration;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.application.APPIOSApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Logger;

public class FolderMonitor implements Runnable {

  private static final Logger log = Logger.getLogger(FolderMonitor.class.getName());
  private IOSServerManager iosServerManager;
  private IOSServerConfiguration iosServerConfiguration;
  private WatchService folderWatcher;
  private final Object stoppedLock;
  private boolean stopped;
  private Thread thread;

  public FolderMonitor(IOSServerConfiguration iosServerConfiguration,
                       IOSServerManager iosServerManager)
      throws IOException {
    this.iosServerConfiguration = iosServerConfiguration;
    this.iosServerManager = iosServerManager;
    stoppedLock = new Object();
    stopped = false;
    init();
    folderWatcher = FileSystems.getDefault().newWatchService();
    Path watchedFolder = Paths.get(iosServerConfiguration.getAppFolderToMonitor());
    try {
      watchedFolder.register(folderWatcher, StandardWatchEventKinds.ENTRY_CREATE,
                             StandardWatchEventKinds.ENTRY_MODIFY,
                             StandardWatchEventKinds.ENTRY_DELETE);
    } catch (NoSuchFileException e) {
      stop();
      log.warning("invalid location: " + new File(iosServerConfiguration.getAppFolderToMonitor())
          .getAbsolutePath());
    }
  }

  private void init() {
    File[] listOfFiles = new File(iosServerConfiguration.getAppFolderToMonitor()).listFiles();
    if (listOfFiles == null) {
      return;
    }
    for (File file : listOfFiles) {
      if (isApp(file)) {
        addApplication(file);
      }
      if (isZip(file)) {
        unzipToWatchedFolder(file);
      }
    }
  }

  @Override
  public void run() {
    synchronized (stoppedLock) {
      while (!stopped) {
        checkForChanges();
        try {
          stoppedLock.wait(1000, 0);
        } catch (InterruptedException ignore) {
        }
      }
    }
  }

  private void checkForChanges() {
    final WatchKey key = folderWatcher.poll();

    if (key != null) {
      for (WatchEvent<?> watchEvent : key.pollEvents()) {
        final Path filePath = (Path) watchEvent.context();
        final WatchEvent.Kind<?> kind = watchEvent.kind();
        log.fine(kind + " : " + filePath);
        handleFileChange(kind,
                         new File(iosServerConfiguration.getAppFolderToMonitor(),
                                  filePath.getFileName().toString()));
      }

      boolean valid = key.reset();
      if (!valid) {
        log.warning("Can't monitor folder anymore; has it been deleted?");
        stop();
      }
    }
  }

  private void handleFileChange(WatchEvent.Kind kind, File file) {
    if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
      if (isApp(file)) {
        log.info("New app found! " + file.getName());
        addApplication(file);
      }
      if (isZip(file)) {
        unzipToWatchedFolder(file);
      }
    } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
      log.info("App modified - no handler implemented!");
    } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
      log.info("App deleted - no handler implemented!");
    }
  }

  private void unzipToWatchedFolder(File file) {
    log.info("Unzipping... " + file.getName());
    try {
      ZipUtils.unzip(file, new File(iosServerConfiguration.getAppFolderToMonitor()));
    } catch (IOException e) {
      log.warning("Problem unzipping " + file.getAbsolutePath() + ", " + e.toString());
    }
  }

  private void addApplication(File file) {
    APPIOSApplication app;
    if (isApp(file)) {
      app = APPIOSApplication.createFrom(file);
    } else {
      app = new APPIOSApplication(file.getAbsolutePath());
    }
    iosServerManager.addSupportedApplication(app);
    log.info("added:\n\t" + app);
  }

  private boolean isApp(File file) {
    if (file.getAbsolutePath().contains("ipa.unzipped")) {
      return false;
    }
    String ext = file.getName();
    return ext.endsWith(".app") || ext.endsWith(".ipa");
  }

  private boolean isZip(File file) {
    String ext = file.getName();
    return ext.endsWith(".zip");
  }

  public void start() {
    thread = new Thread(this);
    thread.start();
  }

  public void stop() {
    synchronized (stoppedLock) {
      stopped = true;
    }
    try {
      if (thread != null) {
        thread.join();
        thread = null;
      }
    } catch (InterruptedException ignore) {
    }
  }
}