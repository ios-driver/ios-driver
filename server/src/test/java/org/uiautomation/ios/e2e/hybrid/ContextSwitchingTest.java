package org.uiautomation.ios.e2e.hybrid;

import java.net.URL;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Pages;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.UIAModels.predicate.ValueCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAKeyboard;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public class ContextSwitchingTest {
  private static String safari =
      "/Applications/Xcode4.5.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator6.0.sdk/Applications/MobileSafari.app";
  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-aut", safari, "-aut",
      SampleApps.getUICatalogFile()};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private RemoteUIADriver nativeDriver = null;
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }

  @Test
  public void findThem() throws Exception {


    IOSCapabilities safari = IOSCapabilities.ipad("Safari");
    safari.setCapability(IOSCapabilities.TIME_HACK, false);

    nativeDriver = new RemoteUIADriver(new URL(url), safari);
    Set<String> handles = nativeDriver.getWindowHandles();

    Assert.assertEquals(handles.size(), 2);


    nativeDriver.quit();
  }
  
  public static void main(String[] args) {
    AppServer appServer =  new WebbitAppServer();
    appServer.start();
    Pages p = new Pages(appServer);
    System.out.println(p.alertsPage);
  }
  
  @Test
  public void seleniumTests() throws Exception {


    IOSCapabilities safari = IOSCapabilities.ipad("Safari");
    safari.setCapability(IOSCapabilities.TIME_HACK, false);

    nativeDriver = new RemoteUIADriver(new URL(url), safari);
    
    Criteria urlAddressBar =
        new AndCriteria(new TypeCriteria(UIAElement.class), new ValueCriteria(
            "Go to this address"));

    nativeDriver.findElement(urlAddressBar).tap();


    Keyboard keyboard = nativeDriver.getKeyboard();

    AppServer appServer =  new WebbitAppServer();
    appServer.start();
    Pages p = new Pages(appServer);
    
    keyboard.sendKeys(p.alertsPage);
    // keyboard.typeString("http://pages.ebay.co.uk/sitemap.html");
    // screenshot();
    ((RemoteUIAKeyboard)keyboard).findElement(new NameCriteria("Go")).tap();
    Set<String> handles = nativeDriver.getWindowHandles();

    Assert.assertEquals(handles.size(), 2);


    nativeDriver.quit();
  }


  @Test
  public void pureNative() throws Exception {
    IOSCapabilities safari = IOSCapabilities.iphone("UICatalog");
    safari.setCapability(IOSCapabilities.TIME_HACK, false);

    nativeDriver = new RemoteUIADriver(new URL(url), safari);
    // System.out.println(nativeDriver.getCapabilities().getBundleId());
    // com.yourcompany.UICatalog
    try {
      Set<String> handles = nativeDriver.getWindowHandles();
      Assert.assertEquals(handles.size(), 1);
      System.out.println("h:" + handles);

      UIAElement el =
          nativeDriver.findElement(new AndCriteria(new TypeCriteria(UIATableCell.class),
              new NameCriteria("Web", MatchingStrategy.starts)));
      el.tap();

      handles = nativeDriver.getWindowHandles();
      System.out.println("h:" + handles);
      Assert.assertEquals(handles.size(), 2);


      UIAElement back =
          nativeDriver.findElement(new AndCriteria(new TypeCriteria(UIAButton.class),
              new NameCriteria("Back")));
      back.tap();
      handles = nativeDriver.getWindowHandles();
      System.out.println("h:" + handles);
      Assert.assertEquals(handles.size(), 1);

    } finally {
      nativeDriver.quit();
    }
  }


  @Test
  public void html() throws Exception {
    IOSCapabilities safari = IOSCapabilities.iphone("UICatalog");
    safari.setCapability(IOSCapabilities.TIME_HACK, false);

    nativeDriver = new RemoteUIADriver(new URL(url), safari);
    try {
      UIAElement webCell =
          nativeDriver.findElement(new AndCriteria(new TypeCriteria(UIATableCell.class),
              new NameCriteria("Web", MatchingStrategy.starts)));
      webCell.tap();
      nativeDriver.switchTo().window("webView_1");

      
      WebElement el = nativeDriver.findElement(By.cssSelector("a[href='http://store.apple.com/']"));
      System.out.println(el.getAttribute("href"));
      el.click();

    } finally {
      nativeDriver.quit();
    }



  }


  @AfterClass
  public void stopServer() throws Exception {
    server.stop();
  }
}
