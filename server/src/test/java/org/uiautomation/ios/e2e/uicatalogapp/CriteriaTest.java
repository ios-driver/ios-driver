package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.LocationCriteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.PropertyEqualCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

public class CriteriaTest extends BaseIOSDriverTest {

  private RemoteIOSDriver driver;

  @BeforeClass
  public void startDriver() {
    IOSCapabilities c = SampleApps.uiCatalogCap();
    c.setSDKVersion("6.1");
    driver = new RemoteIOSDriver(getRemoteURL(), c);
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  private String buttonName = "Buttons, Various uses of UIButton";

  @Test
  public void exactMatch() {

    Criteria c1 = new TypeCriteria(UIATableCell.class);
    PropertyEqualCriteria c2 = new NameCriteria(buttonName);
    Assert.assertEquals(c2.getMatchingStrategy(), MatchingStrategy.exact);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    Assert.assertEquals(element.getName(), buttonName);
  }


  @Test
  public void regexMatch() throws InterruptedException {
    String regex = "Buttons, V[a-z]* uses of UIButton";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    PropertyEqualCriteria c2 = new NameCriteria(regex, MatchingStrategy.regex);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    Assert.assertEquals(element.getName(), buttonName);

  }

  @Test
  public void positionMatch() {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    PropertyEqualCriteria c2 = new NameCriteria(buttonName);
    Assert.assertEquals(c2.getMatchingStrategy(), MatchingStrategy.exact);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);

    UIARect position = element.getRect();

    // top left corner
    Criteria test = new LocationCriteria(position.getX(), position.getY());
    UIAElement res = driver.findElement(test);
    Assert.assertEquals(res.getName(), buttonName);

    // bottom right corner
    int x = position.getX() + position.getWidth() - 1;
    int y = position.getY() + position.getHeight() - 1;
    test = new LocationCriteria(x, y);
    res = driver.findElement(test);
    Assert.assertEquals(res.getName(), buttonName);

    // middle
    x = position.getX() + (position.getWidth() / 2);
    y = position.getY() + (position.getHeight() / 2);
    test = new LocationCriteria(x, y);
    res = driver.findElement(test);
    Assert.assertEquals(res.getName(), buttonName);

  }

}
