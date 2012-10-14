package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAAlert;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIALink;
import org.uiautomation.ios.UIAModels.UIAStaticText;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.exceptions.NoAlertOpenError;
import org.uiautomation.ios.exceptions.UnexpectedAlertOpen;

public class AlertTest extends UICatalogTestsBase {


  private void getAlert(RemoteUIAWindow win) {
    String name = "Alerts";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name, MatchingStrategy.starts);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = win.findElement(c);
    element.tap();

    return;
  }


  @Test(expectedExceptions = UnexpectedAlertOpen.class)
  public void cannotInteractWithAppWhenAlertOpen() throws Exception {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      RemoteUIATarget target = driver.getLocalTarget();
      RemoteUIAApplication app = target.getFrontMostApp();
      RemoteUIAWindow win = app.getMainWindow();
      getAlert(win);

      Criteria c =
          new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Show Simple"));
      UIAElement el = win.findElements(c).get(1);
      // opens an alert.
      el.tap();
      // should throw.The alert prevent interacting with the background.
      el.tap();

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void checkUIAlertView() throws Exception {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      RemoteUIATarget target = driver.getLocalTarget();
      RemoteUIAApplication app = target.getFrontMostApp();
      RemoteUIAWindow win = app.getMainWindow();
      getAlert(win);

      Criteria c =
          new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Show Simple"));
      UIAElement el = win.findElements(c).get(1);
      // opens an alert.
      el.tap();


      UIAAlert alert = target.getAlert();

      // check the alert has all its elements
      alert.findElement(UIAStaticText.class, new NameCriteria("UIAlertView"));
      alert.findElement(UIAStaticText.class, new NameCriteria("<Alert message>"));
      UIAButton ok = alert.findElement(UIAButton.class, new NameCriteria("OK"));

      ok.tap();

      
      Exception expected = null;
      try {
        target.getAlert();
      } catch (NoAlertOpenError e) {
        expected = e;
      }
      Assert.assertNotNull(expected);
      Assert.assertEquals(expected.getClass(), NoAlertOpenError.class);



    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
