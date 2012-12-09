package org.uiautomation.ios.e2e.uicatalogapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATableView;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.EmptyCriteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class RemoteUIAElementTest extends BaseIOSDriverTest {

  private RemoteUIADriver driver;
  private UIAElement element;

  @BeforeClass
  public void startDriver() {
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
  public void findElement() {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(buttonsName);
    Criteria c = new AndCriteria(c1, c2);
    element = driver.findElement(c);
    Assert.assertEquals(element.getName(), buttonsName);
    Assert.assertNull(element.getLabel());
    Assert.assertNull(element.getValue());
  }

  @Test(dependsOnMethods = { "findElement" })
  public void logElementTreeNoScreenshot() throws Exception {
    JSONObject tree = element.logElementTree(null, false);
    Assert.assertTrue(tree.has("tree"));
  }
  
  @Test
  public void logElementTreeRootNoScreenshot() throws Exception {
    JSONObject tree = driver.logElementTree(null, false);
    Assert.assertTrue(tree.has("tree"));
  }

  @Test(dependsOnMethods = { "findElement" })
  public void logElementTreeWithScreenshot() throws Exception {
    File f = new File("logElementTreeWithScreenshotTmp");
    f.delete();
    JSONObject tree = element.logElementTree(f, true);
    Assert.assertTrue(tree.has("tree"));
    Assert.assertTrue(f.exists());
    f.delete();
  }
  @Test
  public void logElementTreeRootWithScreenshot() throws Exception {
    File f = new File("logElementTreeWithScreenshotTmp");
    f.delete();
    JSONObject tree = driver.logElementTree(f, true);
    Assert.assertTrue(tree.has("tree"));
    Assert.assertTrue(f.exists());
    f.delete();
  }

  // sometimes 29, sometimes 31 depending on the timing.and 33 with ios 6...
  @Test(groups = "broken")
  public void findAllElements() throws InterruptedException {
    List<UIAElement> elements = driver.findElements(new EmptyCriteria());
    Assert.assertTrue(elements.size()>40);
  }

  @Test(groups = "broken",enabled=false)
  public void isVisibleTests() {

    Criteria c = new TypeCriteria(UIATableView.class);
    List<UIAElement> elements = driver.findElements(c);

    UIATableView tableView = (UIATableView) elements.get(0);
    List<WebElement> cells = tableView.findElements(By.tagName("UIATableCell"));

    // list of visible components.
    List<String> v = new ArrayList<String>();
    v.add("Buttons, Various uses of UIButton");
    v.add("Controls, Various uses of UIControl");
    v.add("TextFields, Uses of UITextField");
    v.add("SearchBar, Use of UISearchBar");
    v.add("TextView, Use of UITextField");
    v.add("Pickers, Uses of UIDatePicker, UIPickerView");
    v.add("Images, Use of UIImageView");
    v.add("Web, Use of UIWebView");
    v.add("Segment, Various uses of UISegmentedControl");
    v.add("Toolbar, Uses of UIToolbar");

    for (WebElement el : cells) {
      boolean visible = false;
      if (v.contains(el.getAttribute("name"))) {
        visible = true;
      }
      Assert.assertEquals(el.isDisplayed(), visible);
    }
  }

  @Test(enabled=false)
  public void canClickVisibleElement() {
    Criteria invisibleOne = new AndCriteria(new NameCriteria("Buttons, Various uses of UIButton"), new TypeCriteria(
        UIATableCell.class));
    UIAElement element = driver.findElement(invisibleOne);
    element.tap();
  }

  // need to find a test for that.
  // visible doesn't mean visible, but "can be visible after scrolling )
  @Test(expectedExceptions = ElementNotVisibleException.class, groups = "broken", enabled = false)
  public void cannotClickInvisibleElement() {
    RemoteUIADriver driver = null;

   

      Criteria invisibleOne = new AndCriteria(new NameCriteria("Transitions, Shows UIViewAnimationTransitions"),
          new TypeCriteria(UIATableCell.class));
      UIAElement element = driver.findElement(invisibleOne);

      element.tap();

  
  }

  // TODO freynaud element.getName() creates some scrolling.
  @Test(expectedExceptions = StaleElementReferenceException.class)
  public void staleElement() {
    try {
      String name = "Buttons, Various uses of UIButton";
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      Criteria c2 = new NameCriteria(name);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement element = driver.findElement(c);
      // should work.
      element.getName();
      // new screen. The element doesn't exist anymore
      element.tap();
      
      // cannot use a stale element. Exception thrown.
      element.getName();
      Assert.fail("cannot access stale elements");
    } finally {
      UIAButton but = driver.findElement(new AndCriteria(new NameCriteria("Back"),new TypeCriteria(UIAButton.class)));
      but.tap();
    }
  }

}
