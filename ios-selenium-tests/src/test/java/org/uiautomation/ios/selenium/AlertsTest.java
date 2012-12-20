package org.uiautomation.ios.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnhandledAlertException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * no really done right now. it will be possible, but there will be some timing issues.
 */
public class AlertsTest extends BaseSeleniumTest {

  /*@BeforeClass
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
  }  */

  /*@Test(expectedExceptions = UnhandledAlertException.class)
  public void testShouldAllowUsersToAcceptAnAlertManually() throws InterruptedException {
    driver.findElement(By.id("alert")).click();
    Thread.sleep(500);
    driver.findElement(By.id("alert"));
    /*Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    Assert.assertEquals("Testing Alerts", driver.getTitle());
  }

  @Test
  public void testShouldAllowUsersToAcceptAnAlertWithNoTextManually() {
    driver.findElement(By.id("empty-alert")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testShouldGetTextOfAlertOpenedInSetTimeout() throws Exception {
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
    driver.findElement(By.id("alert")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.dismiss();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testShouldAllowAUserToAcceptAPrompt() {
    driver.findElement(By.id("prompt")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testShouldAllowAUserToDismissAPrompt() {
    driver.findElement(By.id("prompt")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.dismiss();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }


  @Test
  public void testShouldAllowAUserToSetTheValueOfAPrompt() {
    driver.findElement(By.id("prompt")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.sendKeys("cheese");
    alert.accept();

    waitFor(elementTextToEqual(driver, By.id("text"), "cheese"));
  }


  @Test
  public void testSettingTheValueOfAnAlertThrows() {
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
    driver.findElement(By.id("alert")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    String value = alert.getText();
    alert.accept();

    assertEquals("cheese", value);
  }

  @Test

  public void testShouldAllowTheUserToGetTheTextOfAPrompt() {
    driver.findElement(By.id("prompt")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    String value = alert.getText();
    alert.accept();

    assertEquals("Enter something", value);
  }


  @Test
  public void testAlertShouldNotAllowAdditionalCommandsIfDismissed() {
    driver.findElement(By.id("alert")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.dismiss();

    try {
      alert.getText();
    } catch (NoAlertPresentException expected) {
    }
  }


  @Test
  public void testShouldAllowUsersToAcceptAnAlertInAFrame() {
    driver.switchTo().frame("iframeWithAlert");

    driver.findElement(By.id("alertInFrame")).click();

    //Alert alert = waitFor(alertToBePresent(driver));
    //alert.accept();

    // If we can perform any action, we're good to go
    //assertEquals("Testing Alerts", driver.getTitle());
  }


  /*@Test
  public void testShouldAllowUsersToAcceptAnAlertInANestedFrame() {
    driver.switchTo().frame("iframeWithIframe").switchTo().frame("iframeWithAlert");

    driver.findElement(By.id("alertInFrame")).click();

    Alert alert = waitFor(alertToBePresent(driver));
    alert.accept();

    // If we can perform any action, we're good to go
    assertEquals("Testing Alerts", driver.getTitle());
  }  */


  /*@Test
  public void testShouldThrowAnExceptionIfAnAlertHasNotBeenDealtWithAndDismissTheAlert() {
    clickOnElementById("alert");

    try {
      System.out.println(driver.getTitle());
      Assert.fail("Expected UnhandledAlertException");
    } catch (UnhandledAlertException e) {
      // this is expected
    }

    // But the next call should be good.
    //Assert.assertEquals("Testing Alerts", driver.getTitle());
  } */


  /*@Test
  public void testSwitchingToMissingAlertThrows() throws Exception {
    try {
      driver.switchTo().alert();
      fail("Expected exception");
    } catch (NoAlertPresentException expected) {
      // Expected
    }
  }


  @Test
  public void testSwitchingToMissingAlertInAClosedWindowThrows() throws Exception {
    String mainWindow = driver.getWindowHandle();
    try {
      driver.findElement(By.id("open-new-window")).click();
      waitFor(windowHandleCountToBe(driver, 2));
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
    driver.get(appServer.whereIs("pageWithOnLoad.html"));

    Alert alert = waitFor(alertToBePresent(driver));
    String value = alert.getText();
    alert.accept();

    assertEquals("onload", value);
    waitFor(elementTextToEqual(driver, By.tagName("p"), "Page with onload event handler"));
  }

  @Test
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
        fail("Expected exception");
      } catch (NoAlertPresentException expected) {
        // Expected
      }

    } finally {
      driver.switchTo().window(onloadWindow);
      waitFor(alertToBePresent(driver)).dismiss();
      driver.close();
      driver.switchTo().window(mainWindow);
      waitFor(
          elementTextToEqual(driver, By.id("open-window-with-onload-alert"), "open new window"));
    }
  }

  @Test
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
      }  */
  /*}

  @Test
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

  @Test
  public void testCanQuitWhenAnAlertIsPresent() {
    driver.get(pages.alertsPage);
    driver.findElement(By.id("alert")).click();
    waitFor(alertToBePresent(driver));

    driver.quit();
  }   */


  private void clickOnElementById(String id) {
    driver.findElement(By.id(id)).click();
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      //
    }
  }
}
