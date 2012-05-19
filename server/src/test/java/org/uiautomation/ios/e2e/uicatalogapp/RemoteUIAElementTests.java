package org.uiautomation.ios.e2e.uicatalogapp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.Name;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATableView;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.ClassCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.EmptyCriteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.exceptions.ElementNotVisibleException;
import org.uiautomation.ios.exceptions.NoSuchElementException;
import org.uiautomation.ios.exceptions.StaleReferenceException;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.tmp.SampleApps;

public class RemoteUIAElementTests {

  private IOSServer server;
  private static String[] args = {"-port", "5555", "-host", "localhost"};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }

  private RemoteUIADriver getDriver() {
    return new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
        SampleApps.cap());
  }

  private RemoteUIAWindow getWindow(RemoteUIADriver driver) {
    RemoteUIATarget target = driver.getLocalTarget();
    RemoteUIAApplication app = target.getFrontMostApp();
    RemoteUIAWindow window = app.getMainWindow();
    return window;
  }

  @Test
  public void canGetMainWindow() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      RemoteUIAWindow win = getWindow(driver);

      UIARect size = win.getRect();
      Assert.assertEquals(size.getX(), 0);
      Assert.assertEquals(size.getY(), 0);
      Assert.assertEquals(size.getHeight(), 480);
      Assert.assertEquals(size.getWidth(), 320);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


  @Test
  public void findElement() {
    RemoteUIADriver driver = null;
    try {
      String name = "Buttons, Various uses of UIButton";
      driver = getDriver();
      RemoteUIAWindow win = getWindow(driver);
      Criteria c1 = new ClassCriteria(UIATableCell.class);
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


  @Test(expectedExceptions = NoSuchElementException.class)
  public void cannotFindElement() {
    RemoteUIADriver driver = null;
    try {
      String name = "I don't exist.";
      driver = getDriver();
      RemoteUIAWindow win = getWindow(driver);
      Criteria c1 = new ClassCriteria(UIATableCell.class);
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
      RemoteUIAWindow win = getWindow(driver);
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
      RemoteUIAWindow win = getWindow(driver);
      Criteria c = new ClassCriteria(UIATableCell.class);
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

  @Test
  public void isVisibleTests() {
    RemoteUIADriver driver = null;

    try {
      driver = getDriver();
      RemoteUIAWindow win = getWindow(driver);

      Criteria c = new ClassCriteria(UIATableView.class);
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
      RemoteUIAWindow win = getWindow(driver);
      Criteria invisibleOne =
          new AndCriteria(new NameCriteria("Buttons, Various uses of UIButton"), new ClassCriteria(
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
  @Test(expectedExceptions = ElementNotVisibleException.class,groups="broken",enabled=false)
  public void cannotClickInvisibleElement() {
    RemoteUIADriver driver = null;

    try {
      driver = getDriver();
      RemoteUIAWindow win = getWindow(driver);

      Criteria invisibleOne =
          new AndCriteria(new NameCriteria("Transitions, Shows UIViewAnimationTransitions"),
              new ClassCriteria(UIATableCell.class));
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
      RemoteUIAWindow win = getWindow(driver);
      Criteria c1 = new ClassCriteria(UIATableCell.class);
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


  @AfterClass
  public void stopServer() throws Exception {
    server.stop();
  }
}
