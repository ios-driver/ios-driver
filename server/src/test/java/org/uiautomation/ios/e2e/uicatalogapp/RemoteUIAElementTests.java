package org.uiautomation.ios.e2e.uicatalogapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATableView;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.EmptyCriteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.exceptions.ElementNotVisibleException;
import org.uiautomation.ios.exceptions.NoSuchElementException;
import org.uiautomation.ios.exceptions.StaleReferenceException;

public class RemoteUIAElementTests extends UICatalogTestsBase {



 


  @Test
  public void findElement() {
    RemoteUIADriver driver = null;
    try {
      String name = "Buttons, Various uses of UIButton";
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      Criteria c2 = new NameCriteria(name);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement element = win.findElement(c);
      Assert.assertEquals(element.getName(), name);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
  
  
  @Test
  public void logElementTreeNoScreenshot() throws Exception {
    RemoteUIADriver driver = null;
    try {
      String name = "Buttons, Various uses of UIButton";
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      Criteria c2 = new NameCriteria(name);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement element = win.findElement(c);
      Assert.assertEquals(element.getName(), name);
      JSONObject tree = element.logElementTree(null,false);
      Assert.assertTrue(tree.has("tree"));
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
  
  @Test
  public void logElementTreeWithScreenshot() throws Exception {
    RemoteUIADriver driver = null;
    try {
      String name = "Buttons, Various uses of UIButton";
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      Criteria c2 = new NameCriteria(name);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement element = win.findElement(c);
      Assert.assertEquals(element.getName(), name);
      File f = new File("logElementTreeWithScreenshotTmp");
      f.delete();
      JSONObject tree = element.logElementTree(f,true);
      Assert.assertTrue(tree.has("tree"));
      Assert.assertTrue(f.exists());
      f.delete();
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


  @Test(expectedExceptions = NoSuchElementException.class)
  public void cannotFindElement() {
    RemoteUIADriver driver = null;
    try {
      String name = "I don't exist.";
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      Criteria c2 = new NameCriteria(name);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement element = win.findElement(c);
      Assert.assertEquals(element.getName(), name);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void findAllElements() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      UIAElementArray<UIAElement> elements = win.findElements(new EmptyCriteria());
      Assert.assertEquals(elements.size(), 31);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }



  @Test
  public void findElementsWithCriteria() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria c = new TypeCriteria(UIATableCell.class);
      UIAElementArray<UIAElement> elements = win.findElements(c);
      Assert.assertEquals(elements.size(), 12);
      for (UIAElement el : elements) {
        Assert.assertTrue(el instanceof UIATableCell);
      }
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(groups ="broken")
  public void isVisibleTests() {
    RemoteUIADriver driver = null;

    try {
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);

      Criteria c = new TypeCriteria(UIATableView.class);
      UIAElementArray<UIAElement> elements = win.findElements(c);

      UIATableView tableView = (UIATableView) elements.get(0);
      UIAElementArray<UIATableCell> cells = tableView.getCells();

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


      for (UIAElement el : cells) {
        boolean visible = false;
        if (v.contains(el.getName())) {
          visible = true;
        }
        Assert.assertEquals(el.isVisible(), visible);
      }

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void canClickVisibleElement() {
    RemoteUIADriver driver = null;

    try {
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria invisibleOne =
          new AndCriteria(new NameCriteria("Buttons, Various uses of UIButton"), new TypeCriteria(
              UIATableCell.class));
      UIAElement element = win.findElement(invisibleOne);

      element.tap();

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  // need to find a test for that.
  // visible doesn't mean visible, but "can be visible after scrolling )
  @Test(expectedExceptions = ElementNotVisibleException.class, groups = "broken", enabled = false)
  public void cannotClickInvisibleElement() {
    RemoteUIADriver driver = null;

    try {
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);

      Criteria invisibleOne =
          new AndCriteria(new NameCriteria("Transitions, Shows UIViewAnimationTransitions"),
              new TypeCriteria(UIATableCell.class));
      UIAElement element = win.findElement(invisibleOne);

      element.tap();

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


  @Test(expectedExceptions = StaleReferenceException.class)
  public void staleElement() {
    RemoteUIADriver driver = null;
    try {
      String name = "Buttons, Various uses of UIButton";
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      Criteria c2 = new NameCriteria(name);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement element = win.findElement(c);

      element.tap();
      // new screen. The element doesn't exist anymore
      Assert.assertFalse(element.isValid());
      // cannot use a stale element. Exception thrown.
      element.getName();
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }



}
