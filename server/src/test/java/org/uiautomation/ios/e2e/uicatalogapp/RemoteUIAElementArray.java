package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.ClassCriteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;

public class RemoteUIAElementArray extends RemoteTestsBase {


  @Test
  public void findElementOnArray() {
    RemoteUIADriver driver = null;
    try {
      String name = "Buttons, Various uses of UIButton";
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      UIAElementArray<UIAElement> cells = win.findElements(new ClassCriteria(UIATableCell.class));
      System.out.println(cells.size());
      UIATableCell cell = (UIATableCell) cells.getFirst(new NameCriteria(name));
      System.out.println(cell);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
