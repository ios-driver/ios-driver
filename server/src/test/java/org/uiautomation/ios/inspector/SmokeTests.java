package org.uiautomation.ios.inspector;


public class SmokeTests {

  private static final int driver_port = 4444;

  public static void main(String[] args) throws Exception {
    IDEServer ide = new IDEServer(driver_port);
    MockedCache cache = new MockedCache();
    ide.setCache(cache);
    ide.start();
    System.out.println("server started. To view some mocked inspector, go to  \n workspace/ios-driver/server/src/test/resources/index.html\n");
  }

}
