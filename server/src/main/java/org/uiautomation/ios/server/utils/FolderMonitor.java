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
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.grid.RegistrationRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
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
    if (isCreate(kind) && isApp(filename)) {
      log.info("New app found!");
      addApplication(filename);
    }
  }

  private void addApplication(File filename) {
    String app = iosServerConfiguration.getAppFolderToMonitor() + File.separator + filename.getName();
    iosServerManager.addSupportedApplication(new IOSApplication(app));
//    if (iosServerConfiguration.getRegistrationURL() != null) {
//      RegistrationRequest
//          request =
//          new RegistrationRequest(iosServerConfiguration.getRegistrationURL(), iosServerConfiguration.getHost(),
//              iosServerConfiguration.getPort(), iosServerManager.getSupportApplicationPaths());
//      request.registerToHub();
//    }
  }

  private boolean isCreate(WatchEvent.Kind kind) {
    return kind.equals(StandardWatchEventKinds.ENTRY_CREATE);
  }

  private boolean isApp(File file) {
    return file.getName().endsWith(".app");
  }

  public void stop() {
    stopped = true;
  }
}
