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

import com.barbarysoftware.watchservice.WatchEvent;
import com.barbarysoftware.watchservice.WatchKey;
import com.barbarysoftware.watchservice.WatchService;
import com.barbarysoftware.watchservice.WatchableFile;

import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.application.APPIOSApplication;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_CREATE;
import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_DELETE;
import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_MODIFY;
import static com.barbarysoftware.watchservice.StandardWatchEventKind.OVERFLOW;

public class FolderMonitor implements Runnable {

  private static final Logger log = Logger.getLogger(FolderMonitor.class.getName());
  private IOSServerManager iosServerManager;
  private IOSServerConfiguration iosServerConfiguration;
  private WatchService folderWatcher;
  private boolean stopped;

  public FolderMonitor(IOSServerConfiguration iosServerConfiguration,
                       IOSServerManager iosServerManager) throws IOException {
    this.iosServerConfiguration = iosServerConfiguration;
    this.iosServerManager = iosServerManager;
    stopped = false;
    init();
    folderWatcher = WatchService.newWatchService();
    WatchableFile
        watchedFolder =
        new WatchableFile(new File(iosServerConfiguration.getAppFolderToMonitor()));
    watchedFolder.register(folderWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
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
    WatchKey key = folderWatcher.poll();

    if (key != null) {
      for (WatchEvent<?> watchEvent : key.pollEvents()) {
        final WatchEvent.Kind<?> kind = watchEvent.kind();
        if (kind == OVERFLOW) {
          continue;
        }

        final WatchEvent<File> ev = (WatchEvent<File>) watchEvent;
        final File filename = ev.context();

        log.fine(kind + " : " + filename);
        handleFileChange(kind, new File(filename.getPath()));
      }

      boolean valid = key.reset();
      if (!valid) {
        log.warning("Can't monitor folder anymore, has it been deleted?");
        stop();
      }
    }
  }

  private void handleFileChange(WatchEvent.Kind kind, File filename) {
    if (!isApp(filename)) {
      return;
    }
    if (kind.equals(ENTRY_CREATE)) {
      log.info("New app found!");
      addApplication(filename);
    } else if (kind.equals(ENTRY_MODIFY)) {
      log.info("App modified - no handler implemented!");
    } else if (kind.equals(ENTRY_DELETE)) {
      log.info("App deleted - no handler implemented!");
    }
  }

  private void addApplication(File filename) {
    APPIOSApplication a = APPIOSApplication.createFrom(filename);
    iosServerManager.addSupportedApplication(a);
  }

  private boolean isApp(File file) {
    if (file.getAbsolutePath().contains("ipa.unzipped")) {
      return false;
    }
    String ext = file.getName();
    return ext.endsWith(".app") || ext.endsWith(".ipa");
  }

  public void stop() {
    stopped = true;
  }
}
