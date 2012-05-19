package org.uiautomation.ios.e2e;

import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public class IDETests {


  private static IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost"};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);

  public static void main(String[] args) throws Exception {
    server = new IOSServer(config);
    server.init(config);
    server.start();
    System.out.println("started");
  }

}
