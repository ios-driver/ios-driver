package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATableView;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;

public class RemoteUIAElementArrayTest extends UICatalogTestsBase {


  @Test
  public void findElementOnMyArray() {
    RemoteUIADriver driver = null;
    try {
      String name = "Buttons, Various uses of UIButton";
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      UIAElementArray<UIAElement> cells = win.findElements(new TypeCriteria(UIATableCell.class));
      UIATableCell cell = (UIATableCell) cells.getFirst(new NameCriteria(name));
      Assert.assertEquals(cell.getName(), name);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


  @Test
  public void findElementOnNativeArray() {
    RemoteUIADriver driver = null;
    try {
      String name = "Buttons, Various uses of UIButton";
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      UIAElementArray<UIAElement> elements =
          win.findElements(new TypeCriteria(UIATableView.class));

      UIATableView tableView = (UIATableView) elements.get(0);
      UIAElementArray<UIATableCell> cells = tableView.getCells();

      UIATableCell cell = cells.getFirst(new NameCriteria(name));
      Assert.assertEquals(cell.getName(), name);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void findElementsOnMyArray() {
    RemoteUIADriver driver = null;
    try {
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      UIAElementArray<UIAElement> cells = win.findElements(new TypeCriteria(UIATableCell.class));
      UIAElementArray<UIAElement> cells2 = cells.getAll(new TypeCriteria(UIATableCell.class));
      Assert.assertEquals(cells.size(), cells2.size());

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void findElementsOnNativeArray() {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      UIATableView tv = (UIATableView) win.findElement(new TypeCriteria(UIATableView.class));
      UIAElementArray<UIATableCell> cells = tv.getCells();

      UIAElementArray<UIATableCell> cells2 = cells.getAll(new TypeCriteria(UIATableCell.class));
      Assert.assertEquals(cells.size(), cells2.size());

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
