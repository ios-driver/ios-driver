package org.uiautomation.ios.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoDriverAfterTest;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WaitingConditions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.elementToExist;
import static org.openqa.selenium.WaitingConditions.pageTitleToBe;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class FrameSwitchingTest extends BaseSeleniumTest {

  private static final int TIMEOUT = 4000;

  // ----------------------------------------------------------------------------------------------
  //
  // Tests that WebDriver doesn't do anything fishy when it navigates to a page
  // with frames.
  //
  // ----------------------------------------------------------------------------------------------
  @Test
  public void testShouldAlwaysFocusOnTheTopMostFrameAfterANavigationEvent() {
    driver.get(pages.framesetPage);
    driver.findElement(By.tagName("frameset")); // Test passes if this does not
    // throw.
  }

  @Test
  public void testShouldNotAutomaticallySwitchFocusToAnIFrameWhenAPageContainingThemIsLoaded() {
    driver.get(pages.iframePage);
    driver.findElement(By.id("iframe_page_heading"));
  }

  // ----------------------------------------------------------------------------------------------
  //
  // Tests that WebDriver can switch to frames as expected.
  //
  // ----------------------------------------------------------------------------------------------
  @Test
  public void testShouldBeAbleToSwitchToAFrameByItsIndex() {
    driver.get(pages.framesetPage);
    driver.switchTo().frame(1);

    assertEquals(driver.findElement(By.id("pageNumber")).getText(), ("2"));
  }

  @Test
  public void testShouldBeAbleToSwitchToAnIframeByItsIndex() {
    driver.get(pages.iframePage);
    driver.switchTo().frame(0);

    assertEquals(driver.findElement(By.name("id-name1")).getAttribute("value"), ("name"));
  }

  @Test
  public void testShouldBeAbleToSwitchToAFrameByItsName() {
    driver.get(pages.framesetPage);
    driver.switchTo().frame("fourth");

    assertEquals(driver.findElement(By.tagName("frame")).getAttribute("name"), ("child1"));
  }

  @Test
  public void testShouldBeAbleToSwitchToAnIframeByItsName() {
    driver.get(pages.iframePage);
    driver.switchTo().frame("iframe1-name");

    assertEquals(driver.findElement(By.name("id-name1")).getAttribute("value"), ("name"));
  }

  @Test
  public void testShouldBeAbleToSwitchToAFrameByItsID() {
    driver.get(pages.framesetPage);
    driver.switchTo().frame("fifth");
    assertEquals(driver.findElement(By.name("windowOne")).getText(), ("Open new window"));
  }

  @Test
  public void testShouldBeAbleToSwitchToAnIframeByItsID() {
    driver.get(pages.iframePage);
    driver.switchTo().frame("iframe1");

    assertEquals(driver.findElement(By.name("id-name1")).getAttribute("value"), ("name"));
  }

  @Test
  //@Ignore({ OPERA, OPERA_MOBILE })
  public void testShouldBeAbleToSwitchToFrameWithNameContainingDot() {
    driver.get(pages.framesetPage);
    driver.switchTo().frame("sixth.iframe1");
    assertTrue(driver.findElement(By.tagName("body")).getText().contains("Page number 3"));
  }

  //@Ignore(value = { SELENESE }, reason = "switchTo().frame(WebElement) not supported with Selenium")
  @Test
  public void testShouldBeAbleToSwitchToAFrameUsingAPreviouslyLocatedWebElement() {
    driver.get(pages.framesetPage);
    WebElement frame = driver.findElement(By.tagName("frame"));
    driver.switchTo().frame(frame);

    assertEquals(driver.findElement(By.id("pageNumber")).getText(), ("1"));
  }

  @Test
  public void testShouldBeAbleToSwitchToAnIFrameUsingAPreviouslyLocatedWebElement() {
    driver.get(pages.iframePage);
    WebElement frame = driver.findElement(By.tagName("iframe"));
    driver.switchTo().frame(frame);

    WebElement element = driver.findElement(By.name("id-name1"));
    assertEquals(element.getAttribute("value"), ("name"));
  }

  //@Ignore(value = { SELENESE }, reason = "switchTo().frame(WebElement) not supported with Selenium")
  @Test
  public void testShouldEnsureElementIsAFrameBeforeSwitching() {
    driver.get(pages.framesetPage);
    WebElement frame = driver.findElement(By.tagName("frameset"));

    try {
      driver.switchTo().frame(frame);
      fail();
    } catch (NoSuchFrameException expected) {
      // Do nothing.
    }
  }

  //@Ignore(ANDROID)
  @Test
  public void testFrameSearchesShouldBeRelativeToTheCurrentlySelectedFrame() {
    driver.get(pages.framesetPage);

    driver.switchTo().frame("second");
    assertEquals(driver.findElement(By.id("pageNumber")).getText(), ("2"));

    try {
      driver.switchTo().frame("third");
      fail();
    } catch (NoSuchFrameException expected) {
      // Do nothing
    }

    driver.switchTo().defaultContent();
    driver.switchTo().frame("third");

    try {
      driver.switchTo().frame("second");
      fail();
    } catch (NoSuchFrameException expected) {
      // Do nothing
    }

    driver.switchTo().defaultContent();
    driver.switchTo().frame("second");
    assertEquals(driver.findElement(By.id("pageNumber")).getText(), ("2"));
  }

  //@Ignore({ ANDROID, OPERA, OPERA_MOBILE })
  @Test
  public void testShouldSelectChildFramesByChainedCalls() {
    driver.get(pages.framesetPage);

    driver.switchTo().frame("fourth").switchTo().frame("child2");
    assertEquals(driver.findElement(By.id("pageNumber")).getText(), ("11"));
  }

  //@Ignore(ANDROID)
  @Test
  public void testShouldThrowFrameNotFoundExceptionLookingUpSubFramesWithSuperFrameNames() {
    driver.get(pages.framesetPage);
    driver.switchTo().frame("fourth");

    try {
      driver.switchTo().frame("second");
      fail("Expected NoSuchFrameException");
    } catch (NoSuchFrameException e) {
      // Expected
    }

  }

  @Test
  public void testShouldThrowAnExceptionWhenAFrameCannotBeFound() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.switchTo().frame("Nothing here");
      fail("Should not have been able to switch");
    } catch (NoSuchFrameException e) {
      // This is expected
    }
  }

  @Test
  public void testShouldThrowAnExceptionWhenAFrameCannotBeFoundByIndex() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.switchTo().frame(27);
      fail("Should not have been able to switch");
    } catch (NoSuchFrameException e) {
      // This is expected
    }
  }

  // ----------------------------------------------------------------------------------------------
  //
  // General frame handling behavior tests
  //
  // ----------------------------------------------------------------------------------------------

  //@Ignore(ANDROID)
  @Test
  public void testShouldContinueToReferToTheSameFrameOnceItHasBeenSelected() {
    driver.get(pages.framesetPage);

    driver.switchTo().frame(2);
    WebElement checkbox = driver.findElement(By.xpath("//input[@name='checky']"));
    checkbox.click();

    // IOS click takes some time to register. Need to wait for the result of that click before
    // continuing.
    waitFor(WaitingConditions.elementSelectionToBe(checkbox,true));

    checkbox.submit();

    // TODO(simon): this should not be needed, and is only here because IE's
    // submit returns too
    // soon.

    waitFor(WaitingConditions.elementTextToEqual(driver, By.xpath("//p"), "Success!"));
  }

  //@Ignore(value = { ANDROID, OPERA, OPERA_MOBILE }, reason = "Android does not detect that the select frame has disappeared")
  @Test
  public void testShouldFocusOnTheReplacementWhenAFrameFollowsALinkToA_TopTargettedPage()
      throws Exception {
    driver.get(pages.framesetPage);

    driver.switchTo().frame(0);
    driver.findElement(By.linkText("top")).click();

    String expectedTitle = "XHTML Test Page";

    waitFor(pageTitleToBe(driver, expectedTitle));
    waitFor(elementToExist(driver, "only-exists-on-xhtmltest"));
  }

  //@Ignore(ANDROID)
  @Test
  public void testShouldAllowAUserToSwitchFromAnIframeBackToTheMainContentOfThePage() {
    driver.get(pages.iframePage);
    driver.switchTo().frame(0);

    try {
      driver.switchTo().defaultContent();
      driver.findElement(By.id("iframe_page_heading"));
    } catch (Exception e) {
      fail("Should have switched back to main content");
    }
  }

  //@Ignore(ANDROID)
  @Test
  public void testShouldAllowTheUserToSwitchToAnIFrameAndRemainFocusedOnIt()
      throws InterruptedException {
    driver.get(pages.iframePage);
    driver.switchTo().frame(0);

    WebElement element = driver.findElement(By.id("submitButton"));
    element.click();
    assertEquals(getTextOfGreetingElement(), ("Success!"));
  }

  public String getTextOfGreetingElement() {
    return waitFor(elementToExist(driver, "greeting")).getText();
  }

  //@Ignore({ OPERA, ANDROID, OPERA_MOBILE })
  @Test
  public void testShouldBeAbleToClickInAFrame() {
    driver.get(pages.framesetPage);
    driver.switchTo().frame("third");

    // This should replace frame "third" ...
    driver.findElement(By.id("submitButton")).click();
    // driver should still be focused on frame "third" ...
    assertEquals(getTextOfGreetingElement(), ("Success!"));
    // Make sure it was really frame "third" which was replaced ...
    driver.switchTo().defaultContent().switchTo().frame("third");
    assertEquals(getTextOfGreetingElement(), ("Success!"));
  }


  //@Ignore({ OPERA, ANDROID, OPERA_MOBILE })
  @Test
  public void testShouldBeAbleToClickInASubFrame() {
    driver.get(pages.framesetPage);
    driver.switchTo().frame("sixth").switchTo().frame("iframe1");

    // This should replace frame "iframe1" inside frame "sixth" ...
    driver.findElement(By.id("submitButton")).click();
    // driver should still be focused on frame "iframe1" inside frame "sixth"
    // ...
    assertEquals(getTextOfGreetingElement(), ("Success!"));
    // Make sure it was really frame "iframe1" inside frame "sixth" which was
    // replaced ...
    driver.switchTo().defaultContent().switchTo().frame("sixth").switchTo().frame("iframe1");
    assertEquals(driver.findElement(By.id("greeting")).getText(), ("Success!"));
  }

  @NoDriverAfterTest
  //@Ignore({ IPHONE })
  @Test
  public void testClosingTheFinalBrowserWindowShouldNotCauseAnExceptionToBeThrown() {
    driver.get(pages.simpleTestPage);
    try {
      driver.close();
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      pw.flush();
      pw.close();
      fail("This is not expected. " + sw);
    }
  }

  //@Ignore(ANDROID)
  @Test
  public void testShouldBeAbleToFindElementsInIframesByXPath() {
    driver.get(pages.iframePage);

    driver.switchTo().frame("iframe1");

    WebElement element = driver.findElement(By.xpath("//*[@id = 'changeme']"));

    assertNotNull(element);
  }

  //@Ignore(ANDROID)
  @Test
  public void testGetCurrentUrl() {
    driver.get(pages.framesetPage);

    driver.switchTo().frame("second");

    String url = appServer.whereIs("page/2");
    assertEquals(driver.getCurrentUrl(), (url + "?title=Fish"));

    url = appServer.whereIs("iframes.html");
    driver.get(pages.iframePage);
    assertEquals(driver.getCurrentUrl(), (url));

    url = appServer.whereIs("formPage.html");
    driver.switchTo().frame("iframe1");
    assertEquals(driver.getCurrentUrl(), (url));
  }

  //@Ignore(value = { ANDROID, OPERA, OPERA_MOBILE })
  ///@JavascriptEnabled
  @Test
  public void testShouldBeAbleToCarryOnWorkingIfTheFrameIsDeletedFromUnderUs()
      throws InterruptedException {
    driver.get(pages.deletingFrame);

    driver.switchTo().frame("iframe1");

    // TODO freynaud {"method":"Page.frameDetached","params":{"frameId":"0.86"}}
    WebElement killIframe = driver.findElement(By.id("killIframe"));
    killIframe.click();
    Thread.sleep(1000);
    driver.switchTo().defaultContent();

    assertFrameNotPresent(driver, "iframe1");

    WebElement addIFrame = driver.findElement(By.id("addBackFrame"));
    addIFrame.click();
    waitFor(elementToExist(driver, "iframe1"));

    driver.switchTo().frame("iframe1");

    try {
      waitFor(elementToExist(driver, "checkbox"));
    } catch (WebDriverException web) {
      fail("Could not find element after switching frame");
    }
  }

  //@Ignore(value = { CHROME, SELENESE }, reason = "These drivers still return frame title.")
  @Test
  public void testShouldReturnWindowTitleInAFrameset() {
    driver.get(pages.framesetPage);
    driver.switchTo().frame("third");
    assertEquals("Unique title", driver.getTitle());
  }

  //@JavascriptEnabled
  @Test
  public void testJavaScriptShouldExecuteInTheContextOfTheCurrentFrame() {
    JavascriptExecutor executor = (JavascriptExecutor) driver;

    driver.get(pages.framesetPage);
    assertTrue((Boolean) executor.executeScript("return window == window.top"));
    driver.switchTo().frame("third");
    assertTrue((Boolean) executor.executeScript("return window != window.top"));
  }

  private void assertFrameNotPresent(WebDriver driver, String locator) {
    long end = System.currentTimeMillis() + TIMEOUT;

    while (System.currentTimeMillis() < end) {
      try {
        driver.switchTo().frame(locator);
      } catch (NoSuchFrameException e) {
        return;
      } finally {
        driver.switchTo().defaultContent();
      }
    }

    fail("Frame did not disappear");
  }
}
