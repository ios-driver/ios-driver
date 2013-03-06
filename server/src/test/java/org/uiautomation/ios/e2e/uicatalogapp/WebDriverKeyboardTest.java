package org.uiautomation.ios.e2e.uicatalogapp;

import java.util.List;

import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.*;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.*;
import org.uiautomation.ios.UIAModels.predicate.*;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

public class WebDriverKeyboardTest extends BaseIOSDriverTest {

    private WebDriver driver;

    @BeforeClass
    public void startDriver() {
        driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.uiCatalogCap());
    }

    @AfterClass
    public void stopDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    private UIATextField getTextField() {
        String name = "TextFields, Uses of UITextField";
        Criteria c1 = new TypeCriteria(UIATableCell.class);
        Criteria c2 = new NameCriteria(name);
        Criteria c = new AndCriteria(c1, c2);
        UIAElement element = ((RemoteIOSDriver)driver).findElement(c);
        element.tap();
        Criteria fieldC = new AndCriteria(new TypeCriteria(UIATextField.class), new NameCriteria("Normal"));
        UIATextField textfield = (UIATextField)((RemoteIOSDriver)driver).findElement(fieldC);
        return textfield;
    }

    @Test
    public void typeBasic() {
        String message = "François 1234";
        UIATextField textfield = getTextField();
        textfield.sendKeys(message);
        Assert.assertEquals(textfield.getValue(), message);
    }

    @Test(enabled=false)
    public void testSendKeysToTextFields() {
        driver.findElement(By.xpath("//UIAStaticText[contains(@name,'TextFields, Uses of UITextField')]")).click();
        List<WebElement> textFields = driver.findElements(By.className("UIATextField"));
        textFields.get(0).sendKeys("first");
        textFields.get(1).sendKeys("second");
        Assert.assertEquals(textFields.get(0).getText(), "first");
        Assert.assertEquals(textFields.get(1).getText(), "second");
    }
}