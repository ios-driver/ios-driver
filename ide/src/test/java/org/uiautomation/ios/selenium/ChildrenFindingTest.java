package org.uiautomation.ios.selenium;


import static org.openqa.selenium.testing.Ignore.Driver.SELENESE;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Pages;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;
import org.openqa.selenium.testing.Ignore;
import org.openqa.selenium.testing.JavascriptEnabled;
import org.testng.Assert;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteMobileSafariDriver;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public class ChildrenFindingTest {
  
  
  private static String safari = "/Applications/Xcode4.5.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator6.0.sdk/Applications/MobileSafari.app";
  private IOSServer server;
  private static String[] args = { "-port", "4444", "-host", "localhost", "-aut", safari };
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private RemoteMobileSafariDriver driver = null;
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";
  private Pages pages;
  private AppServer appServer;

  @BeforeClass
  public void setup() throws Exception {

    server = new IOSServer(config);
    server.start();

    IOSCapabilities safari = IOSCapabilities.ipad("Safari");
    safari.setCapability(IOSCapabilities.TIME_HACK, false);

    driver = new RemoteMobileSafariDriver(url, safari);
    appServer = new WebbitAppServer();
    appServer.start();
    pages = new Pages(appServer);
  }
  
  @AfterClass
  public void tearDown() throws Exception {

    try {
      driver.quit();
    } catch (Exception e) {
      System.err.println("cannot quit properly :" + e.getMessage());
    }
    server.stop();
    appServer.stop();
    
  }
  @Test
  public void testFindElementByXPath() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.name("form2"));
    WebElement child = element.findElement(By.xpath("select"));
    Assert.assertEquals(child.getAttribute("id"), ("2"));
  }

  @Ignore(value = SELENESE, reason = "Apparently Selenium is filtering results")
  @Test
  public void testFindingElementsOnElementByXPathShouldFindTopLevelElements() {
    driver.get(pages.simpleTestPage);
    WebElement parent = driver.findElement(By.id("multiline"));
    List<WebElement> allPs = driver.findElements(By.xpath("//p"));
    List<WebElement> children = parent.findElements(By.xpath("//p"));
    Assert.assertEquals(allPs.size(), children.size());
  }

  @Test
  public void testFindingDotSlashElementsOnElementByXPathShouldFindNotTopLevelElements() {
    driver.get(pages.simpleTestPage);
    WebElement parent = driver.findElement(By.id("multiline"));
    List<WebElement> children = parent.findElements(By.xpath("./p"));
    Assert.assertEquals(1, children.size());
    Assert.assertEquals("A div containing", children.get(0).getText());
  }

  @Test
  public void testFindElementByXPathWhenNoMatch() {
    driver.get(pages.nestedPage);

    WebElement element = driver.findElement(By.name("form2"));
    try {
      element.findElement(By.xpath(".//select/x"));
      fail("Did not expect to find element");
    } catch (NoSuchElementException ignored) {
      // this is expected
    }
  }

  @Test
  public void testfindElementsByXPath() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.name("form2"));
    List<WebElement> children = element.findElements(By.xpath("select/option"));
    Assert.assertEquals(children.size(), (8));
    Assert.assertEquals(children.get(0).getText(), ("One"));
    Assert.assertEquals(children.get(1).getText(), ("Two"));
  }

  @Test
  public void testfindElementsByXPathWhenNoMatch() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.name("form2"));
    List<WebElement> children = element.findElements(By.xpath(".//select/x"));
    assertEquals(0, children.size());
  }

  @Test
  public void testfindElementByName() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.name("form2"));
    WebElement child = element.findElement(By.name("selectomatic"));
    Assert.assertEquals(child.getAttribute("id"), ("2"));
  }

  @Test
  public void testfindElementsByName() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.name("form2"));
    List<WebElement> children = element.findElements(By.name("selectomatic"));
    assertEquals(children.size(), (2));
  }

  @Test
  public void testfindElementById() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.name("form2"));
    WebElement child = element.findElement(By.id("2"));
    assertEquals(child.getAttribute("name"), ("selectomatic"));
  }

  @Test
  public void testfindElementByIdWhenMultipleMatchesExist() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.id("test_id_div"));
    WebElement child = element.findElement(By.id("test_id"));
    assertEquals(child.getText(), ("inside"));
  }

  @Test
  public void testfindElementByIdWhenNoMatchInContext() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.id("test_id_div"));
    try {
      element.findElement(By.id("test_id_out"));
      fail();
    } catch (NoSuchElementException e) {
      // This is expected
    }
  }

  @Test
  public void testfindElementsById() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.name("form2"));
    List<WebElement> children = element.findElements(By.id("2"));
    assertEquals(children.size(), (2));
  }

  @Test
  public void testFindElementByLinkText() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.name("div1"));
    WebElement child = element.findElement(By.linkText("hello world"));
    assertEquals(child.getAttribute("name"), ("link1"));
  }

  @Test
  public void testFindElementsByLinkTest() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.name("div1"));
    List<WebElement> elements = element.findElements(By.linkText("hello world"));

    assertEquals(2, elements.size());
    assertEquals(elements.get(0).getAttribute("name"), ("link1"));
    assertEquals(elements.get(1).getAttribute("name"), ("link2"));
  }

  @Test
  public void testfindElementsByLinkText() {
    driver.get(pages.nestedPage);
    WebElement element = driver.findElement(By.name("div1"));
    List<WebElement> children = element.findElements(
        By.linkText("hello world"));
    assertEquals(children.size(), (2));
  }

  @Test
  public void testShouldFindChildElementsByClassName() {
    driver.get(pages.nestedPage);
    WebElement parent = driver.findElement(By.name("classes"));

    WebElement element = parent.findElement(By.className("one"));

    assertEquals("Find me", element.getText());
  }

  @Test
  public void testShouldFindChildrenByClassName() {
    driver.get(pages.nestedPage);
    WebElement parent = driver.findElement(By.name("classes"));

    List<WebElement> elements = parent.findElements(By.className("one"));

    assertEquals(2, elements.size());
  }

  @Test
  public void testShouldFindChildElementsByTagName() {
    driver.get(pages.nestedPage);
    WebElement parent = driver.findElement(By.name("div1"));

    WebElement element = parent.findElement(By.tagName("a"));

    assertEquals("link1", element.getAttribute("name"));
  }

  @Test
  public void testShouldFindChildrenByTagName() {
    driver.get(pages.nestedPage);
    WebElement parent = driver.findElement(By.name("div1"));

    List<WebElement> elements = parent.findElements(By.tagName("a"));

    assertEquals(2, elements.size());
  }

  @JavascriptEnabled
  @Test
  public void testShouldBeAbleToFindAnElementByCssSelector() {
    driver.get(pages.nestedPage);
    WebElement parent = driver.findElement(By.name("form2"));

    WebElement element = parent.findElement(By.cssSelector("*[name=\"selectomatic\"]"));

    assertEquals("2", element.getAttribute("id"));
  }

  @JavascriptEnabled
  @Test
  public void testShouldBeAbleToFindAnElementsByCssSelector() {
    driver.get(pages.nestedPage);
    WebElement parent = driver.findElement(By.name("form2"));

    List<WebElement> elements = parent.findElements(By.cssSelector("*[name=\"selectomatic\"]"));

    assertEquals(2, elements.size());
  }
}
