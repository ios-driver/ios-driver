package org.uiautomation.ios.selenium;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.UnhandledAlertException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.alertToBePresent;
import static org.openqa.selenium.WaitingConditions.elementTextToEqual;
import static org.openqa.selenium.WaitingConditions.windowHandleCountToBe;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * no really done right now. it will be possible, but there will be some timing issues.
 */
public class AlertsTest extends BaseSeleniumTest {

  @BeforeClass
  public void setUp() throws Exception {
    driver.get(pages.alertsPage);
  }

  @Test
  public void testShouldBeAbleToOverrideTheWindowAlertMethod() throws InterruptedException {
    ((JavascriptExecutor) driver).executeScript(
        "window.alert = function(msg) { document.getElementById('text').innerHTML = msg; }");
    driver.findElement(By.id("alert")).click();
    // TODO freynaud find out why reloading the page right after the click makes the alert appear.
    Thread.sleep(1000);
    driver.get(pages.alertsPage);
  }

  @Test//(expectedExceptions = UnhandledAlertException.class)
  public void testShouldAllowUsersToAcceptAnAlertManually() throws InterruptedException {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("alert")).click();
    Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }

  @Test
  public void testShouldAllowUsersToAcceptAnAlertWithNoTextManually() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("empty-alert")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testShouldGetTextOfAlertOpenedInSetTimeout() throws Exception {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("slow-alert")).click();

    // DO NOT WAIT OR SLEEP HERE.
    // This is a regression test for a bug where only the first switchTo call would throw,
    // and only if it happens before the alert actually loads.
    Alert alert = driver.switchTo().alert();
    try {
      assertEquals("Slow", alert.getText());
    } finally {
      alert.accept();
    }
  }


  @Test
  public void testShouldAllowUsersToDismissAnAlertManually() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("alert")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.dismiss();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testShouldAllowAUserToAcceptAPrompt() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("prompt")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testShouldAllowAUserToDismissAPrompt() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("prompt")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.dismiss();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testShouldAllowAUserToSetTheValueOfAPrompt() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("prompt")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.sendKeys("cheese");
    alert.accept();

    waitFor(elementTextToEqual(driver, By.id("text"), "cheese"));
  }


  @Test
  public void testSettingTheValueOfAnAlertThrows() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("alert")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    try {
      alert.sendKeys("cheese");
      fail("Expected exception");
    } catch (ElementNotVisibleException expected) {
    } finally {
      alert.accept();
    }
  }


  @Test
  public void testShouldAllowTheUserToGetTheTextOfAnAlert() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("alert")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    String value = alert.getText();
    alert.accept();

    assertEquals("cheese", value);
  }

  @Test

  public void testShouldAllowTheUserToGetTheTextOfAPrompt() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("prompt")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    String value = alert.getText();
    alert.accept();

    assertEquals("Enter something", value);
  }


  @Test
  public void testAlertShouldNotAllowAdditionalCommandsIfDismissed() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("alert")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.dismiss();

    try {
      alert.getText();
    } catch (NoAlertPresentException expected) {
    }
  }

  @Test
  public void testAlertInFrameKeepSelectedFrame() {
    driver.get(pages.alertsPage);
    driver.switchTo().frame("iframeWithAlert");

    driver.findElement(By.id("alertInFrame")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
    driver.switchTo().defaultContent();
    driver.switchTo().frame("iframeWithIframe").switchTo().frame("iframeWithAlert");

    driver.findElement(By.id("alertInFrame")).click();

    alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }

  @Test
  public void testShouldAllowUsersToAcceptAnAlertInAFrame() {
    driver.get(pages.alertsPage);
    driver.switchTo().frame("iframeWithAlert");

    driver.findElement(By.id("alertInFrame")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testShouldAllowUsersToAcceptAnAlertInANestedFrame() {
    driver.get(pages.alertsPage);
    driver.switchTo().frame("iframeWithIframe").switchTo().frame("iframeWithAlert");

    driver.findElement(By.id("alertInFrame")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testShouldThrowAnExceptionIfAnAlertHasNotBeenDealtWithAndDismissTheAlert() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("alert")).click();
    Alert alert = null;
    try {
      alert = waitFor(alertToBePresent(driver));
      driver.getTitle();
      Assert.fail("Expected UnhandledAlertException");
    } catch (UnhandledAlertException e) {
      alert.dismiss();
    }

    // But the next call should be good.
    Assert.assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testSwitchingToMissingAlertThrows() throws Exception {
    driver.get(pages.alertsPage);
    try {
      driver.switchTo().alert();
      fail("Expected exception");
    } catch (NoAlertPresentException expected) {
      // Expected
    }
  }


  @Test(enabled = false, description = "bug, missing feature")
  public void testSwitchingToMissingAlertInAClosedWindowThrows() throws Exception {
    String mainWindow = driver.getWindowHandle();
    try {
      driver.findElement(By.id("open-new-window")).click();
      waitFor(windowHandleCountToBe(driver, 2));
      Thread.sleep(10000);

      driver.getCurrentUrl();
      driver.switchTo().window("newwindow").close();

      try {
        alertToBePresent(driver).call();
        fail("Expected exception");
      } catch (NoSuchWindowException expected) {
        // Expected
      }

    } finally {
      driver.switchTo().window(mainWindow);
      waitFor(elementTextToEqual(driver, By.id("open-new-window"), "open new window"));
    }
  }


  @Test
  public void testPromptShouldUseDefaultValueIfNoKeysSent() {
    driver.findElement(By.id("prompt-with-default")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    waitFor(elementTextToEqual(driver, By.id("text"), "This is a default value"));
  }


  @Test
  public void testPromptShouldHaveNullValueIfDismissed() {
    driver.findElement(By.id("prompt-with-default")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.dismiss();

    waitFor(elementTextToEqual(driver, By.id("text"), "null"));
  }


  @Test
  public void testHandlesTwoAlertsFromOneInteraction() {
    driver.findElement(By.id("double-prompt")).click();

    Alert alert1 = waitFor(alertToBePresent(driver));
    alert1.sendKeys("brie");
    alert1.accept();

    Alert alert2 = waitFor(alertToBePresent(driver));
    alert2.sendKeys("cheddar");
    alert2.accept();

    waitFor(elementTextToEqual(driver, By.id("text1"), "brie"));
    waitFor(elementTextToEqual(driver, By.id("text2"), "cheddar"));
  }


  @Test
  public void testShouldHandleAlertOnPageLoad() {
    driver.findElement(By.id("open-page-with-onload-alert")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    String value = alert.getText();
    alert.accept();

    assertEquals("onload", value);
    waitFor(elementTextToEqual(driver, By.tagName("p"), "Page with onload event handler"));
  }


  @Test
  public void testShouldHandleAlertOnPageLoadUsingGet() {
    try {
      driver.get(appServer.whereIs("pageWithOnLoad.html"));
    } catch (UnhandledAlertException e) {
      System.out.println("there was an alert.");
    }

    Alert alert = waitFor(alertToBePresent(driver));
    String value = alert.getText();
    alert.accept();

    assertEquals("onload", value);
    waitFor(elementTextToEqual(driver, By.tagName("p"), "Page with onload event handler"));
  }

  @Test(enabled = false)
  public void testShouldNotHandleAlertInAnotherWindow() {
    String mainWindow = driver.getWindowHandle();
    String onloadWindow = null;
    try {
      driver.findElement(By.id("open-window-with-onload-alert")).click();

      Set<String> allWindows = driver.getWindowHandles();
      allWindows.remove(mainWindow);
      assertEquals(1, allWindows.size());
      onloadWindow = allWindows.iterator().next();

      try {
        waitFor(alertToBePresent(driver), 5, TimeUnit.SECONDS);
        //fail("Expected exception");
      } catch (NoAlertPresentException expected) {
        // Expected
      }

    } finally {
      waitFor(alertToBePresent(driver)).dismiss();
      driver.close();
      driver.switchTo().window(mainWindow);
      waitFor(
          elementTextToEqual(driver, By.id("open-window-with-onload-alert"), "open new window"));
    }
  }

  @Test(enabled = false)
  public void testShouldHandleAlertOnPageUnload() {
    driver.findElement(By.id("open-page-with-onunload-alert")).click();
    driver.navigate().back();

    Alert alert = waitFor(alertToBePresent(driver));
    String value = alert.getText();
    alert.accept();

    assertEquals("onunload", value);
    waitFor(elementTextToEqual(driver, By.id("open-page-with-onunload-alert"), "open new page"));
  }

  @Test
  public void testShouldHandleAlertOnWindowClose() {
      /*if (TestUtilities.isFirefox(driver) &&
          TestUtilities.isNativeEventsEnabled(driver) &&
          TestUtilities.getEffectivePlatform().is(Platform.LINUX)) {
        System.err.println("x_ignore_nofocus can cause a firefox crash here. Ignoring test. See issue 2987.");
        assumeTrue(false);
      }
      String mainWindow = driver.getWindowHandle();
      try {
        driver.findElement(By.id("open-window-with-onclose-alert")).click();
        waitFor(windowHandleCountToBe(driver, 2));
        driver.switchTo().window("onclose").close();

        Alert alert = waitFor(alertToBePresent(driver));
        String value = alert.getText();
        alert.accept();

        assertEquals("onunload", value);

      } finally {
        driver.switchTo().window(mainWindow);
        waitFor(elementTextToEqual(driver, By.id("open-window-with-onclose-alert"), "open new window"));
      }    */
  }

  // TODO freynaud was ok before?
  @Test(enabled = false)
  public void testIncludesAlertInUnhandledAlertException() {
    driver.findElement(By.id("alert")).click();
    waitFor(alertToBePresent(driver));
    try {
      driver.getTitle();
      fail("Expected UnhandledAlertException");
    } catch (UnhandledAlertException e) {
      Alert alert = e.getAlert();
      assertNotNull(alert);
      assertEquals("cheese", alert.getText());
    }
  }

  @Test(enabled = false)
  public void testCanQuitWhenAnAlertIsPresent() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("alert")).click();
    waitFor(alertToBePresent(driver));

    driver.quit();
  }


}
