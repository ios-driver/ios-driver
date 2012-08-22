package org.uiautomation.ios.server;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.instruments.CommunicationChannel;
import org.uiautomation.ios.server.instruments.InstrumentsManager;

public class ServerSideSession extends Session {

  private final IOSApplication application;
  private final InstrumentsManager instruments;



  public ServerSideSession(IOSApplication application,int serverPort) {
    super(UUID.randomUUID().toString());
    this.application = application;

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        forceStop();
      }
    });
    instruments = new InstrumentsManager(serverPort);
  }

  public IOSApplication getApplication() {
    return application;
  }

  public CommunicationChannel communication() {
    return instruments.communicate();
  }

  public void stop() {
    instruments.stop();
  }

  public void forceStop() {
    instruments.forceStop();
  }

  public File getOutputFolder() {
    return instruments.getOutput();
  }

  public InstrumentsManager getInstruments() {
    return instruments;

  }

  public void start(IOSCapabilities capabilities) {
    instruments.startSession(IOSDevice.iPhoneSimulator, "5.1", "en_GB", "en",
        application.getApplicationPath(), getSessionId(), true, new ArrayList<String>());

  }

}
