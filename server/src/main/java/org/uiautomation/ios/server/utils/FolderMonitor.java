/*
 * Copyright 2012 ios-driver committers.
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
package org.uiautomation.ios.server.utils;

import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.application.APPIOSApplication;

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
  private boolean stopped;

  public FolderMonitor(IOSServerConfiguration iosServerConfiguration, IOSServerManager iosServerManager) throws IOException {
    this.iosServerConfiguration = iosServerConfiguration;
    this.iosServerManager = iosServerManager;
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
      log.warning("invalid location: " + iosServerConfiguration.getAppFolderToMonitor());
    }
  }

  private void init() {
    File[] listOfFiles = new File(iosServerConfiguration.getAppFolderToMonitor()).listFiles();
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
    while (!stopped) {
      checkForChanges();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void checkForChanges() {
    final WatchKey key = folderWatcher.poll();

    if (key != null) {
      for (WatchEvent<?> watchEvent : key.pollEvents()) {
        final WatchEvent<Path> ev = (WatchEvent<Path>) watchEvent;
        final Path filename = ev.context();
        final WatchEvent.Kind<?> kind = watchEvent.kind();
        log.fine(kind + " : " + filename);
        handleFileChange(kind, filename.toFile());
      }

      boolean valid = key.reset();
      if (!valid) {
        log.warning("Can't monitor folder anymore, has it been deleted?");
        stop();
      }
    }
  }

  private void handleFileChange(WatchEvent.Kind kind, File filename) {

    if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
      if (isApp(filename)) {
        log.info("New app found! " + filename.getName());
        addApplication(filename);
      }
      if (isZip(filename)) {
        unzipToWatchedFolder(filename);
      }
    } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
      log.info("App modified - no handler implemented!");
    } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
      log.info("App deleted - no handler implemented!");
    }
  }

  private void unzipToWatchedFolder(File filename) {
    log.info("Unzipping... " + filename.getName());
    try {
      ZipUtils.unzip(filename, new File(iosServerConfiguration.getAppFolderToMonitor()));
    } catch (IOException e) {
      log.warning("Problem unzipping " + filename.getName() + ", " + e.toString());
    }
  }

  private void addApplication(File filename) {
    if (isApp(filename)) {
      iosServerManager.addSupportedApplication(APPIOSApplication.createFrom(filename));
    } else {
      iosServerManager.addSupportedApplication(new APPIOSApplication(filename.getAbsolutePath()));
    }
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

  public void stop() {
    stopped = true;
  }
}