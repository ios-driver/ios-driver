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

package org.uiautomation.ios.server;

import com.beust.jcommander.JCommander;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.inspector.IDEServlet;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.configuration.Configuration;
import org.uiautomation.ios.server.grid.SelfRegisteringRemote;
import org.uiautomation.ios.server.instruments.communication.curl.CURLBasedCommunicationChannel;
import org.uiautomation.ios.server.servlet.ApplicationsServlet;
import org.uiautomation.ios.server.servlet.ArchiveServlet;
import org.uiautomation.ios.server.servlet.CapabilitiesServlet;
import org.uiautomation.ios.server.servlet.DeviceServlet;
import org.uiautomation.ios.server.servlet.IOSServlet;
import org.uiautomation.ios.server.servlet.InstrumentsLogServlet;
import org.uiautomation.ios.server.servlet.ResourceServlet;
import org.uiautomation.ios.server.servlet.StaticResourceServlet;
import org.uiautomation.ios.server.utils.FolderMonitor;
import org.uiautomation.ios.server.utils.ZipUtils;
import org.uiautomation.ios.utils.BuildInfo;
import org.uiautomation.ios.utils.IOSVersion;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSServer {

  public static final String DRIVER = IOSServerManager.class.getName();
  private static final Logger log = Logger.getLogger(IOSServer.class.getName());
  private final IOSServerConfiguration options;
  private boolean initialized = false;
  private Server server;
  private IOSServerManager driver;
  private FolderMonitor folderMonitor;
  private SelfRegisteringRemote selfRegisteringRemote;

  public IOSServer(IOSServerConfiguration options) {
    this.options = options;
  }

  public IOSServer(String[] args) {
    IOSServerConfiguration options = new IOSServerConfiguration();
    new JCommander(options, args);
    this.options = options;
  }

  public static void main(String[] args) throws Exception {
    final IOSServer server = new IOSServer(args);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          server.stop();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    try {
      server.start();
    } catch (Exception e) {
      log.log(Level.SEVERE, "cannot start ios-driver server: " + e, e);
      Runtime.getRuntime().exit(1);
    }
  }

  private void init() {
    initialized = true;
    Configuration.BETA_FEATURE = options.isBeta();
    Configuration.SIMULATORS_ENABLED = options.hasSimulators();
    initDriver();
    initServer();
  }

  private void initDriver() {
    driver = new IOSServerManager(options);
    for (String app : this.options.getSupportedApps()) {
      File appFile = new File(app);
      if (Configuration.BETA_FEATURE && !appFile.exists()) {
        // if an url download and extract it
        try {
          // TODO freynaud ?
          appFile = ZipUtils.extractAppFromURL(null);
        } catch (IOException ignore) {
          log.fine("url: " + app + ": " + ignore);
        }
      }
      if (appFile == null || !appFile.exists()) {
        throw new WebDriverException(app + " isn't an IOS app.");
      }
      driver.addSupportedApplication(APPIOSApplication.createFrom(appFile));
    }

    StringBuilder b = new StringBuilder();
    b.append(String.format("\nBeta features enabled (enabled by -beta flag): %b", Configuration.BETA_FEATURE));
    b.append(String.format("\nSimulator enabled : %b", Configuration.SIMULATORS_ENABLED));
    b.append(String.format("\nInspector: http://0.0.0.0:%d/inspector/", options.getPort()));
    b.append(String.format("\nTests can access the server at http://0.0.0.0:%d/wd/hub", options.getPort()));
    b.append(String.format("\nServer status: http://0.0.0.0:%d/wd/hub/status", options.getPort()));
    b.append(String.format("\nConnected devices: http://0.0.0.0:%d/wd/hub/devices/all", options.getPort()));
    b.append(String.format("\nApplications: http://0.0.0.0:%d/wd/hub/applications/all", options.getPort()));
    b.append(String.format("\nCapabilities: http://0.0.0.0:%d/wd/hub/capabilities/all", options.getPort()));
    b.append(String.format("\nMonitoring '%s' for new applications", options.getAppFolderToMonitor()));
    b.append(String.format("\nArchived apps: %s", driver.getApplicationStore().getFolder().getAbsolutePath()));
    b.append("\nBuild info: " + BuildInfo.toBuildInfoString());
    b.append("\nRunning on: " + driver.getHostInfo().getOSInfo());
    b.append("\nUsing java: " + driver.getHostInfo().getJavaVersion());
    if (Configuration.SIMULATORS_ENABLED) {
      addSimulatorDetails(b);
    }

    b.append("\n\nApplications :\n--------------- \n");
    for (APPIOSApplication app : driver.getSupportedApplications()) {
      b.append("\t" + app + "\n");
    }
    log.info(b.toString());
  }

  private void addSimulatorDetails(StringBuilder b) {
    File xcodeInstall = driver.getHostInfo().getXCodeInstall();
    String hostSDK = driver.getHostInfo().getSDK();
    b.append(String.format("\nUsing Xcode install: %s", driver.getHostInfo().getXCodeInstall().getPath()));
    b.append(String.format("\nUsing instruments: %s", driver.getHostInfo().getInstrumentsVersion()));
    b.append(String.format("\nUsing iOS version %s", hostSDK));

    boolean safari = false;
    // automatically add safari for host SDK and above as instruments starts simulator on host SDK version
    for (String s : driver.getHostInfo().getInstalledSDKs()) {
      IOSVersion version = new IOSVersion(s);
      if (version.isGreaterOrEqualTo("6.0")) {
        safari = true;
        driver.addSupportedApplication(copyOfSafari(xcodeInstall, s));
      }
    }
    if (safari) {
      b.append("\niOS >= 6.0. Safari and hybrid apps are supported.");
    } else {
      b.append("\niOS < 6.0. Safari and hybrid apps are NOT supported.");
    }
  }

  private void initServer() {
    String host = System.getProperty("ios-driver.host");
    if (host == null){
      host = "0.0.0.0";
    }
    server = new Server(new InetSocketAddress(host, options.getPort()));

    ServletContextHandler wd = new ServletContextHandler(server, "/wd/hub", true, false);
    wd.addServlet(CURLBasedCommunicationChannel.UIAScriptServlet.class, "/uiascriptproxy/*");
    wd.addServlet(InstrumentsLogServlet.class, "/log/*");
    wd.addServlet(IOSServlet.class, "/*");
    wd.addServlet(ResourceServlet.class, "/resources/*");
    wd.addServlet(DeviceServlet.class, "/devices/*");
    wd.addServlet(ApplicationsServlet.class, "/applications/*");
    wd.addServlet(CapabilitiesServlet.class, "/capabilities/*");
    wd.addServlet(ArchiveServlet.class, "/archive/*");
    wd.getServletContext().getContextHandler().setMaxFormContentSize(500000);
    wd.setAttribute(DRIVER, driver);

    ServletContextHandler statics = new ServletContextHandler(server, "/static", true, false);
    statics.addServlet(StaticResourceServlet.class, "/*");

    ServletContextHandler extra = new ServletContextHandler(server, "/", true, false);
    extra.addServlet(IDEServlet.class, "/inspector/*");
    extra.setAttribute(DRIVER, driver);

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{wd, statics, extra});
    server.setHandler(handlers);
  }

  public static File getTmpIOSFolder(){
    File f =  new File(System.getProperty("user.home") + "/.ios-driver/");
    f.mkdirs();
    return f;
  }

  // TODO freynaud - if xcode change, the safari copy should be wiped out.
  private APPIOSApplication copyOfSafari(File xcodeInstall, String sdk) {
    File copy = new File(getTmpIOSFolder().getAbsolutePath(),"safariCopies/safari-" + sdk + ".app");
    if (!copy.exists()) {
      File safariFolder = APPIOSApplication.findSafariLocation(xcodeInstall, sdk);
      copy.mkdirs();
      try {
        FileUtils.copyDirectory(safariFolder, copy);
      } catch (IOException e) {
        log.warning("Cannot create the copy of safari : " + e.getMessage());
      }
    }
    return new APPIOSApplication(copy.getAbsolutePath());
  }

  public void start() throws Exception {
    if (!initialized) {
      initialized = true;
      init();
    }
    if (!server.isRunning()) {
      server.start();
    }
    startFolderMonitor();
    startHubRegistration();
  }

  private void startFolderMonitor() {
    if (options.getAppFolderToMonitor() != null) {
      try {
        folderMonitor = new FolderMonitor(options, driver);
        folderMonitor.start();
      } catch (IOException e) {
        log.warning("Couldn't monitor the given folder: " + options.getAppFolderToMonitor());
      }
    }
  }

  private void startHubRegistration() {
    if (options.getRegistrationURL() != null) {
      selfRegisteringRemote = new SelfRegisteringRemote(options, driver);
      selfRegisteringRemote.start();
    }
  }

  public void stop() throws Exception {
    if (!initialized) {
      return;
    }
    if (selfRegisteringRemote != null) {
      try {
        selfRegisteringRemote.stop();
        selfRegisteringRemote = null;
      } catch (Exception e) {
        log.warning("exception stopping: " + e);
      }
    }
    if (folderMonitor != null) {
      try {
        folderMonitor.stop();
        folderMonitor = null;
      } catch (Exception e) {
        log.warning("exception stopping: " + e);
      }
    }
    if (driver != null) {
      try {
        driver.stop();
        driver = null;
      } catch (Exception e) {
        log.warning("exception stopping: " + e);
      }
    }
    if (server != null) {
      try {
        server.stop();
        server = null;
      } catch (Exception e) {
        log.warning("exception stopping: " + e);
      }
    }
  }
}
