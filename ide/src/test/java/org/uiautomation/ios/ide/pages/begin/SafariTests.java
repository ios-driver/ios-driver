package org.uiautomation.ios.ide.pages.begin;

import java.io.File;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.UIAModels.predicate.ValueCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public class SafariTests {
  private static String safari =
      "/Applications/Xcode4.5.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator6.0.sdk/Applications/MobileSafari.app";
  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-aut", safari};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private RemoteUIADriver nativeDriver = null;
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }

  @Test
  public void safari() throws Exception {


    IOSCapabilities safari = IOSCapabilities.ipad("Safari");
    safari.setCapability(IOSCapabilities.TIME_HACK, false);

    nativeDriver = new RemoteUIADriver(url, safari);



    Criteria urlAddressBar =
        new AndCriteria(new TypeCriteria(UIAElement.class), new ValueCriteria("Go to this address"));

    nativeDriver.findElement(urlAddressBar).tap();


    UIAKeyboard keyboard = nativeDriver.getLocalTarget().getFrontMostApp().getKeyboard();
    keyboard.typeString("http://pages.ebay.co.uk/sitemap.html");
    keyboard.findElement(new NameCriteria("Go")).tap();

    Set<String> handles = nativeDriver.getWindowHandles();

    String webViewHandle = null;
    for (String handle : handles) {
      if (handle.startsWith("webView")) {
        webViewHandle = handle;
      }
    }

    if (webViewHandle == null) {
      Assert.fail("no web handle.");
    }

    //nativeDriver.setWindow(webViewHandle);
    
    WebElement el =
        nativeDriver.findElement(By.cssSelector("a[href='http://reviews.ebay.co.uk/']"));
   
    // embeded.findElement(By.cssSelector("a[href='http://reviews.ebay.co.uk/']"));

    /*
     * WebInspector webdriver = new WebInspector(nativeDriver); Node document =
     * webdriver.getDocument(); webdriver.getCache().setContextToBase(document);
     * 
     * Criteria urlAddressBar = new AndCriteria(new TypeCriteria(UIAElement.class), new
     * ValueCriteria("Go to this address"));
     * 
     * nativeDriver.findElement(urlAddressBar).tap();
     * 
     * 
     * UIAKeyboard keyboard = nativeDriver.getLocalTarget().getFrontMostApp().getKeyboard();
     * keyboard.typeString("http://ebay.co.uk"); //
     * keyboard.typeString("http://pages.ebay.co.uk/sitemap.html"); // screenshot();
     * keyboard.findElement(new NameCriteria("Go")).tap(); webdriver.getCache().waitForPageToLoad();
     * 
     * // RemoteWebElement searchInput = webdriver.findElementById(null, "gh-ac"); RemoteWebElement
     * findOutMore = webdriver.findElementById(null, "gf-p"); RemoteWebElement reviews =
     * webdriver.findElementByCSSSelector(null, "a[href='http://reviews.ebay.co.uk/']");
     */
    //nativeDriver.setWindow("nativeView");
    
    nativeDriver.quit();
  }



  Criteria hide = new AndCriteria(new TypeCriteria(UIAButton.class), new NameCriteria(
      "Hide keyboard"));

  private int index = 0;

  private void screenshot() {
    File f = new File(index + ".png");
    nativeDriver.takeScreenshot(f.getAbsolutePath());
    Reporter.log("<img src='" + f.getAbsolutePath() + "'/>");
    index++;
  }


  @AfterClass
  public void stopServer() throws Exception {
    server.stop();
  }
}
