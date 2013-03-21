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

package org.uiautomation.ios.server;

import com.beust.jcommander.JCommander;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.inspector.IDEServlet;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.configuration.Configuration;
import org.uiautomation.ios.server.grid.RegistrationRequest;
import org.uiautomation.ios.server.servlet.ApplicationsServlet;
import org.uiautomation.ios.server.servlet.ArchiveServlet;
import org.uiautomation.ios.server.servlet.CapabilitiesServlet;
import org.uiautomation.ios.server.servlet.DeviceServlet;
import org.uiautomation.ios.server.servlet.IOSServlet;
import org.uiautomation.ios.server.servlet.ResourceServlet;
import org.uiautomation.ios.server.servlet.StaticResourceServlet;
import org.uiautomation.ios.server.servlet.UIAScriptServlet;
import org.uiautomation.ios.server.utils.FolderMonitor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class IOSServer {

  public static final String DRIVER = IOSServerManager.class.getName();
  public static final boolean debugMode = true;
  private static final Logger log = Logger.getLogger(IOSServer.class.getName());
  private Server server;
  private IOSServerConfiguration options;
  private IOSServerManager driver;
  private FolderMonitor folderMonitor;

  public IOSServer(IOSServerConfiguration options) {
    init(options);

  }

  public IOSServer(String[] args) {
    init(args);
  }

  public static void main(String[] args) throws Exception {
    IOSServer server = new IOSServer(args);
    server.start();
    IOSServerConfiguration options = server.getOptions();
    IOSServerManager driver = server.getDriver();
    if (options.getRegistrationURL() != null) {
      RegistrationRequest
          request =
          new RegistrationRequest(options, driver);
      try {
        request.registerToHub();
      } catch (Exception e) {
        System.out.println(e.toString());
      }

    }
  }

  public IOSServerConfiguration getOptions() {
    return options;
  }

  public IOSServerManager getDriver() {
    return driver;
  }

  private void init(String[] args) {
    IOSServerConfiguration options = new IOSServerConfiguration();
    new JCommander(options, args);
    init(options);
  }

  private void init(IOSServerConfiguration options) {
    this.options = options;
    Configuration.BETA_FEATURE = options.isBeta();
    server = new Server(new InetSocketAddress("0.0.0.0", options.getPort()));

    ServletContextHandler wd = new ServletContextHandler(server, "/wd/hub", true, false);
    wd.addServlet(UIAScriptServlet.class, "/uiascriptproxy/*");
    wd.addServlet(IOSServlet.class, "/*");
    wd.addServlet(ResourceServlet.class, "/resources/*");
    wd.addServlet(DeviceServlet.class, "/devices/*");
    wd.addServlet(ApplicationsServlet.class, "/applications/*");
    wd.addServlet(CapabilitiesServlet.class, "/capabilities/*");
    wd.addServlet(ArchiveServlet.class, "/archive/*");

    ServletContextHandler statics = new ServletContextHandler(server, "/static", true, false);
    statics.addServlet(StaticResourceServlet.class, "/*");

    ServletContextHandler extra = new ServletContextHandler(server, "/", true, false);
    extra.addServlet(IDEServlet.class, "/inspector/*");

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{wd, statics, extra});
    server.setHandler(handlers);

    driver = new IOSServerManager(options);
    for (String app : this.options.getSupportedApps()) {
      driver.addSupportedApplication(new APPIOSApplication(app));
    }

    startFolderMonitor();

    StringBuilder b = new StringBuilder();
    b.append("\nBeta features enabled ( enabled by -beta flag ): " + Configuration.BETA_FEATURE);
    b.append("\nInspector: http://0.0.0.0:" + options.getPort() + "/inspector/");
    b.append("\ntests can access the server at http://0.0.0.0:" + options.getPort() + "/wd/hub");
    b.append("\nserver status: http://0.0.0.0:" + options.getPort() + "/wd/hub/status");
    b.append("\nConnected devices: http://0.0.0.0:" + options.getPort() + "/wd/hub/devices/all");
    b.append("\nApplications: http://0.0.0.0:" + options.getPort() + "/wd/hub/applications/all");
    b.append("\nCapabilities: http://0.0.0.0:" + options.getPort() + "/wd/hub/capabilities/all");
    b.append("\nMonitoring '" + options.getAppFolderToMonitor() + "' for new applications");
    b.append("\nusing xcode install : " + driver.getHostInfo().getXCodeInstall());
    b.append("\nusing IOS version " + driver.getHostInfo().getSDK());
    b.append("\nArchived apps " + driver.getApplicationStore().getFolder().getAbsolutePath());

    boolean safari = false;
    // automatically add safari for SDK 6.0 and above.
    for (String s : driver.getHostInfo().getInstalledSDKs()) {
      Float version = Float.parseFloat(s);
      if (version >= 6L) {
        safari = true;
        driver.addSupportedApplication(
            APPIOSApplication.findSafariLocation(driver.getHostInfo().getXCodeInstall(), s));
      }
    }
    if (safari) {
      b.append("\nios >= 6.0. Safari and hybrid apps are supported.");
    } else {
      b.append("\nios < 6.0. Safari and hybrid apps are NOT supported.");
    }

    b.append("\n\nApplications :\n--------------- \n");
    for (APPIOSApplication app : driver.getSupportedApplications()) {
      b.append("\tCFBundleName=" + (app.getMetadata(IOSCapabilities.BUNDLE_NAME).isEmpty() ? app
          .getMetadata(IOSCapabilities.BUNDLE_DISPLAY_NAME) : app
                                        .getMetadata(IOSCapabilities.BUNDLE_NAME)));
      String version = app.getMetadata(IOSCapabilities.BUNDLE_VERSION);
      if (version != null && !version.isEmpty()) {
        b.append(",CFBundleVersion=" + version);
      }
      b.append("\n");
    }
    log.info(b.toString());

    wd.setAttribute(DRIVER, driver);
    extra.setAttribute(DRIVER, driver);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          server.stop();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

  }

  private void startFolderMonitor() {
    if (options.getAppFolderToMonitor() != null) {
      try {
        folderMonitor = new FolderMonitor(options, driver);
        new Thread(folderMonitor).start();
      } catch (IOException e) {
        log.warning("Couldn't monitor the given folder: " + options.getAppFolderToMonitor());
      }
    }
  }

  public void start() throws Exception {
    if (!server.isRunning()) {
      server.start();
    }
  }

  public void stop() throws Exception {
    if (folderMonitor != null) {
      folderMonitor.stop();
    }
    driver.stop();
    server.stop();
  }

}
