package org.uiautomation.ios.e2e.uicatalogapp;

import org.openqa.selenium.By;
import org.openqa.selenium.Keyboard;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATextField;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

public class UIAKeyboardSmokeTest extends BaseIOSDriverTest {

  private RemoteIOSDriver driver;
  private UIATextField textfield;

  @BeforeClass
  public void startDriver() {
    IOSCapabilities caps = SampleApps.uiCatalogCap();
    driver = new RemoteIOSDriver(getRemoteURL(), caps);
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test(expectedExceptions = NoSuchElementException.class)
  public void throwsIfKeyboardNotPresent() {
    driver.getKeyboard();
  }

  private UIATextField getTextField() {
    String name = "TextFields, Uses of UITextField";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    element.tap();
    Criteria
        fieldC =
        new AndCriteria(new TypeCriteria(UIATextField.class), new NameCriteria("Normal"));
    textfield = (UIATextField) driver.findElement(fieldC);
    return textfield;
  }


  @Test(dependsOnMethods = {"throwsIfKeyboardNotPresent"})
  public void typeBasic() {

    String message = "Francois 1234";

    UIATextField textfield = getTextField();
    textfield.tap();

    Keyboard keyboard = driver.getKeyboard();
    keyboard.sendKeys(message);

    Assert.assertEquals(textfield.getValue(), message);

  }


  @Test(dependsOnMethods = {"typeBasic"})
  public void sendKeys() {

    textfield.clear();
    textfield.sendKeys("ç");
    textfield.sendKeys("é");
    textfield.sendKeys("Ē");
    textfield.clear();
    textfield.sendKeys("François !");

    System.out.println(textfield.getAttribute("value"));
    //Assert.assertEquals("ç",textfield.getAttribute("value"));

    /*WebElement c = driver.findElement(By.name("c"));

    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", false);
    el.sendKeys("B");
*/

  /*Actions builder = new Actions(driver);

    builder.sendKeys("test");
    builder.perform();*/

  }

}
