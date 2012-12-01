package org.uiautomation.ios.e2e.uicatalogapp;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAApplication;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIAWindow;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class FindElementTest extends BaseIOSDriverTest {

  private RemoteUIADriver driver;

  @BeforeClass
  public void startDriver() {
    long start = System.currentTimeMillis();
    driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  private String buttonsName = "Buttons, Various uses of UIButton";

  @Test
  public void findElementByCriteria() throws InterruptedException {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(buttonsName);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    Assert.assertEquals(element.getName(), buttonsName);
  }

  @Test
  public void findElementByTagName() throws InterruptedException {
    WebElement element = driver.findElement(By.tagName("UIATableCell"));
    Assert.assertEquals(element.getAttribute("name"), buttonsName);
  }

  @Test
  public void findElementsCriteria() throws InterruptedException {
    Criteria cell = new TypeCriteria(UIATableCell.class);
    List<UIAElement> elements = driver.findElements(cell);
    Assert.assertEquals(elements.size(), 12);
  }

  @Test
  public void findElementsByTagName() throws InterruptedException {
    List<WebElement> elements = driver.findElements(By.tagName("UIATableCell"));
    Assert.assertEquals(elements.size(), 12);
  }

  @Test(expectedExceptions = NoSuchElementException.class)
  public void findElementNoResult() throws InterruptedException {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria("I don't exist.");
    Criteria c = new AndCriteria(c1, c2);
    driver.findElement(c);
    Assert.fail("should have thrown");
  }

  @Test
  public void findElementsNoResult() throws InterruptedException {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria("I don't exist.");
    Criteria c = new AndCriteria(c1, c2);
    List<UIAElement> elements = driver.findElements(c);
    Assert.assertEquals(elements.size(), 0);
  }

  @Test
  public void findElementOnElementCriteria() throws InterruptedException {
    UIAApplication app = (UIAApplication) driver.findElement(By.tagName("UIAApplication"));
    UIAElement element = app.findElement(new NameCriteria(buttonsName));
    Assert.assertEquals(element.getName(), buttonsName);
    Assert.assertTrue(element instanceof UIATableCell);
  }

  @Test
  public void findElementOnElementTagName() throws InterruptedException {
    UIAApplication app = (UIAApplication) driver.findElement(By.tagName("UIAApplication"));
    WebElement element = app.findElement(By.tagName("UIATableCell"));
    Assert.assertEquals(element.getAttribute("name"), buttonsName);
    Assert.assertTrue(element instanceof UIATableCell);
  }

  @Test
  public void findElementsOnElementTagName() throws InterruptedException {
    UIAApplication app = (UIAApplication) driver.findElement(By.tagName("UIAApplication"));
    List<WebElement> elements = app.findElements(By.tagName("UIATableCell"));
    Assert.assertEquals(elements.size(), 12);
  }

  @Test
  public void findElementsOnElementCriteria() throws InterruptedException {
    UIAApplication app = (UIAApplication) driver.findElement(By.tagName("UIAApplication"));
    List<UIAElement> elements = app.findElements(new TypeCriteria(UIATableCell.class));
    Assert.assertEquals(elements.size(), 12);
  }

  @Test
  public void findElementsOnElementNoResult() throws InterruptedException {
    UIAWindow window = (UIAWindow) driver.findElement(By.tagName("UIAWindow"));
    List<WebElement> elements = window.findElements(By.tagName("UIAButton"));
    Assert.assertEquals(elements.size(), 0);
  }

  @Test
  public void findElementXpath() throws InterruptedException {
    WebElement element = driver.findElement(By.xpath("//UIATableCell"));
    Assert.assertEquals(element.getAttribute("name"), "Buttons, Various uses of UIButton");
  }

  @Test
  public void findElementsXpath() throws InterruptedException {
    List<WebElement> elements = driver.findElements(By.xpath("//UIATableCell"));
    Assert.assertEquals(elements.size(), 12);
  }

}
