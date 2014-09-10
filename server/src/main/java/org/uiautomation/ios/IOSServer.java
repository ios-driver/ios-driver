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

package org.uiautomation.ios;

import com.google.common.base.Throwables;

import com.beust.jcommander.JCommander;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.libimobiledevice.ios.driver.binding.raw.JNAInit;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.JsonToBeanConverter;
import org.uiautomation.ios.application.APPIOSApplication;
import org.uiautomation.ios.application.MobileSafariLocator;
import org.uiautomation.ios.command.configuration.Configuration;
import org.uiautomation.ios.command.uiautomation.ServerStatusNHandler;
import org.uiautomation.ios.grid.StoppableRegisteringRemote;
import org.uiautomation.ios.inspector.IDEServlet;
import org.uiautomation.ios.instruments.commandExecutor.CURLIAutomationCommandExecutor;
import org.uiautomation.ios.servlet.ApplicationsServlet;
import org.uiautomation.ios.servlet.ArchiveServlet;
import org.uiautomation.ios.servlet.CapabilitiesServlet;
import org.uiautomation.ios.servlet.DeviceServlet;
import org.uiautomation.ios.servlet.IOSServlet;
import org.uiautomation.ios.servlet.InstrumentsLogServlet;
import org.uiautomation.ios.servlet.ResourceServlet;
import org.uiautomation.ios.servlet.ServerManagerServlet;
import org.uiautomation.ios.servlet.StaticResourceServlet;
import org.uiautomation.ios.utils.BuildInfo;
import org.uiautomation.ios.utils.FolderMonitor;
import org.uiautomation.ios.utils.IOSVersion;
import org.uiautomation.ios.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSServer {

  public static final String DRIVER = IOSServerManager.class.getName();
  public static final String SERVER = "serverInstance";
  private static final Logger log = Logger.getLogger(IOSServer.class.getName());
  private final IOSServerConfiguration options;
  private boolean initialized = false;
  private Server server;
  private IOSServerManager driver;
  private FolderMonitor folderMonitor;
  private StoppableRegisteringRemote remote;
  private List<Callable<Boolean>> beforeShutdownHooks = new CopyOnWriteArrayList();
  private List<Callable<Boolean>> afterShutdownHooks = new CopyOnWriteArrayList();

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
          if (server.getDriver() != null) {
            server.getDriver().stop();
          }
        } catch (Exception e) {
         log.log(Level.SEVERE,"error in shutdown hook",e);
        }
      }
    });

    try {
      server.start();
    } catch (Exception e) {
      log.log(Level.SEVERE, "cannot start ios-driver server.",e);
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
          URL u = new URL(app);
          appFile = ZipUtils.extractAppFromURL(u);
        } catch (IOException ignore) {
          log.fine("url: " + app + ": " + ignore);
        }
      }
      if (appFile == null || !appFile.exists()) {
        throw new WebDriverException(app + " isn't an IOS app.");
      }
      driver.addSupportedApplication(APPIOSApplication.createFrom(appFile));
    }

    p(String.format("version:%s", BuildInfo.getAttribute("sha")));
    p(String
          .format("Beta features enabled (enabled by -real flag): %b", Configuration.BETA_FEATURE));
    p(String.format("Simulator enabled : %b", Configuration.SIMULATORS_ENABLED));
    p(String.format("Inspector: http://0.0.0.0:%d/inspector/", options.getPort()));
    p(String.format("Tests can access the server at http://0.0.0.0:%d/wd/hub",
                    options.getPort()));
    p(String.format("Server status: http://0.0.0.0:%d/wd/hub/status", options.getPort()));
    p(String.format("Connected devices: http://0.0.0.0:%d/wd/hub/devices/all",
                    options.getPort()));
    p(String.format("Applications: http://0.0.0.0:%d/wd/hub/applications/all",
                    options.getPort()));
    p(String.format("Capabilities: http://0.0.0.0:%d/wd/hub/capabilities/all",
                    options.getPort()));
    p(
        String.format("Monitoring '%s' for new applications", options.getAppFolderToMonitor()));
    p(String.format("Archived apps: %s",
                    driver.getApplicationStore().getFolder().getAbsolutePath()));
    p("Build info: " + BuildInfo.toBuildInfoString());
    p("Running on: " + driver.getHostInfo().getOSInfo());
    p("Using java: " + driver.getHostInfo().getJavaVersion());
    if (Configuration.SIMULATORS_ENABLED) {
      addSimulatorDetails();
    }

    p("Applications :");
    for (APPIOSApplication app : driver.getSupportedApplications()) {
      p("\t" + app);
    }
  }

  private void p(String msg) {
    System.out.println(msg);
    log.fine(msg);
  }

  public IOSServerManager getDriver() {
    return driver;
  }

  private void addSimulatorDetails() {
    File xcodeInstall = driver.getHostInfo().getXCodeInstall();
    String hostSDK = driver.getHostInfo().getSDK();
    p(String.format("Using Xcode install: %s",
                    driver.getHostInfo().getXCodeInstall().getPath()));
    p(String.format("Using instruments: %s", driver.getHostInfo().getInstrumentsVersion()));
    p(String.format("Using iOS version %s", hostSDK));

    boolean safari = false;
    // automatically add safari for host SDK and above as instruments starts simulator on host SDK version
    for (String s : driver.getHostInfo().getInstalledSDKs()) {
      IOSVersion version = new IOSVersion(s);
      if (version.isGreaterOrEqualTo("6.0")) {
        safari = true;
        driver.addSupportedApplication(MobileSafariLocator.locateSafariInstall(s));
      }
    }
    if (safari) {
      p("iOS >= 6.0. Safari and hybrid apps are supported.");
    } else {
      p("iOS < 6.0. Safari and hybrid apps are NOT supported.");
    }
  }

  private void initServer() {
    String host = System.getProperty("ios-driver.host");
    if (host == null) {
      host = "0.0.0.0";
    }
    server = new Server(new InetSocketAddress(host, options.getPort()));

    ServletContextHandler wd = new ServletContextHandler(server, "/wd/hub", true, false);
    wd.addServlet(CURLIAutomationCommandExecutor.UIAScriptServlet.class, "/uiascriptproxy/*");
    wd.addServlet(InstrumentsLogServlet.class, "/log/*");
    wd.addServlet(IOSServlet.class, "/*");
    wd.addServlet(ResourceServlet.class, "/resources/*");
    wd.addServlet(DeviceServlet.class, "/devices/*");
    wd.addServlet(ApplicationsServlet.class, "/applications/*");
    wd.addServlet(CapabilitiesServlet.class, "/capabilities/*");
    wd.addServlet(ArchiveServlet.class, "/archive/*");
    wd.addServlet(ServerManagerServlet.class, "/manage/*");

    wd.getServletContext().getContextHandler().setMaxFormContentSize(500000);
    wd.setAttribute(DRIVER, driver);
    wd.setAttribute(SERVER, this);

    ServletContextHandler statics = new ServletContextHandler(server, "/static", true, false);
    statics.addServlet(StaticResourceServlet.class, "/*");

    ServletContextHandler extra = new ServletContextHandler(server, "/", true, false);
    extra.addServlet(IDEServlet.class, "/inspector/*");
    for (String clazz : options.getServlets()) {
      try {
        Class c = Class.forName(clazz);
        String path = "/extra/" + c.getSimpleName() + "/*";
        extra.addServlet(c, "/extra/" + c.getSimpleName() + "/*");
        log.info("Servlet " + c + " visible @ " + path);
      } catch (ClassNotFoundException e) {
        throw new WebDriverException(
            "cannot plug servlet " + clazz + ". Cause : " + e.getMessage());
      }
    }
    extra.setAttribute(DRIVER, driver);

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{wd, statics, extra});
    server.setHandler(handlers);

  }

  public static File getTmpIOSFolder() {
    File f = new File(System.getProperty("user.home") + "/.ios-driver/");
    f.mkdirs();
    return f;
  }


  public void start() throws Exception {
    if (!initialized) {
      JNAInit.init();
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

  private void startHubRegistration() throws Exception {
    if (options.getRegistrationURL() != null) {

      String url = options.getRegistrationURL();
      URL hub = new URL(url);
      org.openqa.grid.common.RegistrationRequest
          registrationRequest =
          new org.openqa.grid.common.RegistrationRequest();

      JSONObject status = new ServerStatusNHandler.StatusGenerator(driver).generate();
      JSONArray caps = status.getJSONArray(ServerStatusNHandler.SUPPORTED_APPS);

      JsonToBeanConverter convertor = new JsonToBeanConverter();
      for (int i = 0; i < caps.length(); i++) {
        Capabilities c = convertor.convert(Capabilities.class, caps.get(i).toString());
        DesiredCapabilities c2 = new DesiredCapabilities(c);
        registrationRequest.addDesiredCapability(c2);
      }

      registrationRequest.getConfiguration()
          .put(org.openqa.grid.common.RegistrationRequest.AUTO_REGISTER, true);
      registrationRequest.getConfiguration()
          .put(org.openqa.grid.common.RegistrationRequest.REGISTER_CYCLE, 5000);
      registrationRequest.getConfiguration()
          .put(org.openqa.grid.common.RegistrationRequest.PROXY_CLASS, options.getProxy());

      registrationRequest.getConfiguration()
          .put(org.openqa.grid.common.RegistrationRequest.HUB_HOST, hub.getHost());
      registrationRequest.getConfiguration()
          .put(org.openqa.grid.common.RegistrationRequest.HUB_PORT, hub.getPort());
      registrationRequest.getConfiguration()
          .put(org.openqa.grid.common.RegistrationRequest.REMOTE_HOST,
               "http://" + options.getHost() + ":" + options.getPort());
      registrationRequest.getConfiguration()
          .put(org.openqa.grid.common.RegistrationRequest.MAX_SESSION, 1);

      remote = new StoppableRegisteringRemote(registrationRequest);

      remote.startRegistrationProcess();
    }
  }


  public void beforeShutDown() {
    log.info("server is about to shutdown");
    if (remote != null) {
      remote.stopRegistrationProcess();
    }
    for (Callable<Boolean> call : beforeShutdownHooks) {
      try {
        call.call();
      } catch (Exception e) {
        log.warning("Shuddown hook failed :" + e.getMessage());
      }
    }
  }

  public void afterShutDown() {
    for (Callable<Boolean> call : afterShutdownHooks) {
      try {
        call.call();
      } catch (Exception e) {
        log.warning("after shuddown hook failed :" + e.getMessage());
      }
    }
  }

  public void stopGracefully() throws Exception {
    if (!initialized) {
      return;
    }
    beforeShutDown();
    if (driver != null) {
      driver.stopGracefully();
    }
    stop();
    afterShutDown();
    log.info("server stopped");

  }

  public void stop() throws Exception {
    if (!initialized) {
      return;
    }
    if (remote != null) {
      try {
        remote.stopRegistrationProcess();
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
      } catch (Exception e) {
        log.warning("exception stopping: " + e);
      }
    }
    if (server != null) {
      try {
        server.stop();
      } catch (Exception e) {
        log.warning("exception stopping: " + e);
      }
    }
  }

  public boolean isRunning() {
    if (server == null) {
      return false;
    }
    return server.isRunning();
  }

  public void addBeforeShutdownHook(Callable<Boolean> call) {
    beforeShutdownHooks.add(call);
  }

  public void addAfterShutdownHook(Callable<Boolean> call) {
    afterShutdownHooks.add(call);
  }
}
