package org.uiautomation.ios.e2e.uicatalogapp;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnhandledAlertException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAAlert;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAStaticText;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class AlertTest extends BaseIOSDriverTest {

  private RemoteUIADriver driver;

  @BeforeClass
  public void startDriver() {
    driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
    goToAlertScreen();
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  private void goToAlertScreen() {
    String name = "Alerts";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name, MatchingStrategy.starts);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    element.tap();
  }

  @Test(expectedExceptions = UnhandledAlertException.class)
  public void cannotInteractWithAppWhenAlertOpen() throws Exception {
    Criteria
        c =
        new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Show Simple"));
    UIAElement el = driver.findElements(c).get(1);
    // opens an alert.
    el.tap();
    try {
      el.tap();
      Assert.fail("shouldn't click behind alerts.");
    } finally {
      driver.getAlert().getCancelButton().tap();
    }
  }

  @Test(expectedExceptions = NoAlertPresentException.class)
  public void checkUIAlertView() throws Exception {

    Criteria
        c =
        new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Show Simple"));
    UIAElement el = driver.findElements(c).get(1);
    // opens an alert.
    el.tap();

    UIAAlert alert = driver.getAlert();

    // check the alert has all its elements
    alert.findElement(UIAStaticText.class, new NameCriteria("UIAlertView"));
    alert.findElement(UIAStaticText.class, new NameCriteria("<Alert message>"));
    UIAButton ok = alert.findElement(UIAButton.class, new NameCriteria("OK"));

    ok.tap();

    driver.getAlert();

  }
}
