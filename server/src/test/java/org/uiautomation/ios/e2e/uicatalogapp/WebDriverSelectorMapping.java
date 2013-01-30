package org.uiautomation.ios.e2e.uicatalogapp;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;

public class WebDriverSelectorMapping extends BaseIOSDriverTest {

  private RemoteWebDriver driver;

  @BeforeClass
  public void startDriver() {
    driver = new RemoteWebDriver(getRemoteURL(), SampleApps.intlMountainsCap("en"));
    WebElement element = driver.findElement(By.className("UIATableCell"));
    element.click();


  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  private
  String
      english =
      "Mountain 1 was first climbed on 29 May 1953 and has a height of 8,848 meters";
  private String partial = "first climbed on 29 May 1953";


  @Test
  public void testCanFindByName() {
    By b = By.name(english);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("name"), english);
  }

  @Test
  public void testCanFindByLabel() {
    By b = By.linkText("label=" + english);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("label"), english);
  }

  @Test
  public void testCanFindByValue() {
    By b = By.linkText("value=" + english);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("value"), english);
  }

  @Test
  public void testCanFindByPartialName() {
    By b = By.partialLinkText("name=" + partial);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("name"), english);
  }

  @Test
  public void testCanFindByRegexName() {
    By b = By.partialLinkText("name=first climbed on (.*) and has a height");
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("name"), english);
  }


  @Test
  public void testCanFindByPartialLabel() {
    By b = By.partialLinkText("label=" + partial);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("label"), english);
  }

  @Test
  public void testCanFindByPartialValue() {
    By b = By.partialLinkText("value=" + partial);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("value"), english);
  }

  @Test(expectedExceptions = InvalidSelectorException.class)
  public void testCannotFindById() {
    By b = By.id("someid");
    driver.findElement(b);
  }

  @Test
  public void testCanFindByNamel10n() {
    By b = By.partialLinkText("name=l10n('sentenceFormat')");
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("name"), english);
  }

  @Test
  public void testCanFindByLabell10n() {
    By b = By.partialLinkText("label=l10n('sentenceFormat')");
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("label"), english);
  }

}
