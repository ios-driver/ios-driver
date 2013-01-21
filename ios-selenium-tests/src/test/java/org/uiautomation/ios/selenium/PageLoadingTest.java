package org.uiautomation.ios.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WaitingConditions;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.pageTitleToBe;

public class PageLoadingTest extends BaseSeleniumTest {

  @Test
  public void testShouldWaitForDocumentToBeLoaded() {
    driver.get(pages.simpleTestPage);

    Assert.assertEquals(driver.getTitle(), "Hello WebDriver");
  }

  @Test
  public void testShouldFollowRedirectsSentInTheHttpResponseHeaders() {
    driver.get(pages.redirectPage);

    Assert.assertEquals(driver.getTitle(), "We Arrive Here");
  }

  @Test
  public void testShouldFollowMetaRedirects() throws Exception {
    driver.get(pages.metaRedirectPage);
    Assert.assertEquals(driver.getTitle(), "We Arrive Here");

  }


  @Test
  public void testShouldBeAbleToGetAFragmentOnTheCurrentPage() {
    driver.get(pages.xhtmlTestPage);
    driver.get(pages.xhtmlTestPage + "#text");
    driver.findElement(By.id("id1"));
  }

  @Test(enabled = false)
  public void testShouldBeAbleToGetAFragmentOnTheCurrentPageBackForward() {
    driver.get(pages.xhtmlTestPage);
    driver.get(pages.xhtmlTestPage + "#text");
    driver.findElement(By.id("id1"));
    driver.navigate().back();
    driver.navigate().forward();
  }

  @Test(enabled = false)
  // throw a timeout exception
  public void testShouldReturnWhenGettingAUrlThatDoesNotResolve() {
    try {
      // Of course, we're up the creek if this ever does get registered
      driver.get("http://www.thisurldoesnotexist.comx/");
    } catch (IllegalStateException e) {

    }
  }

  @Test(enabled = false)
  // return by throwing a timeout.
  public void testShouldReturnWhenGettingAUrlThatDoesNotConnect() {
    // Here's hoping that there's nothing here. There shouldn't be
    driver.get("http://localhost:3001");
  }

  @Test
  public void testShouldBeAbleToLoadAPageWithFramesetsAndWaitUntilAllFramesAreLoaded() {
    driver.get(pages.framesetPage);

    driver.switchTo().frame(0);
    WebElement pageNumber = driver.findElement(By.cssSelector("span[id='pageNumber']"));
    Assert.assertEquals(pageNumber.getText().trim(), "1");

    driver.switchTo().defaultContent().switchTo().frame(1);
    pageNumber = driver.findElement(By.cssSelector("span[id='pageNumber']"));
    Assert.assertEquals(pageNumber.getText().trim(), "2");
  }

  @Test(enabled = false)
  public void testShouldDoNothingIfThereIsNothingToGoBackTo() {
    String originalTitle = driver.getTitle();
    driver.get(pages.formPage);
    driver.navigate().back(); // stuck here.
    // Assert.assertEquals(driver.getTitle(), anyOf(equalTo(originalTitle),
    // equalTo("We Leave From Here")));
  }

  @Test
  public void testShouldBeAbleToNavigateBackInTheBrowserHistory() {
    driver.get(pages.formPage);

    driver.findElement(By.id("imageButton")).submit();
    Assert.assertEquals(driver.getTitle(), "We Arrive Here");

    driver.navigate().back();
    Assert.assertEquals(driver.getTitle(), "We Leave From Here");
  }

  /* @Ignore(value = {SAFARI, SELENESE}, issues = {3771}) */
  @Test
  public void testShouldBeAbleToNavigateBackInTheBrowserHistoryInPresenceOfIframes()
      throws InterruptedException {
    driver.get(pages.xhtmlTestPage);

    driver.findElement(By.name("sameWindow")).click();

    waitFor(pageTitleToBe(driver, "This page has iframes"));

    Assert.assertEquals(driver.getTitle(), ("This page has iframes"));

    driver.navigate().back();

    Assert.assertEquals(driver.getTitle(), ("XHTML Test Page"));

  }

  /* @Ignore(value = {ANDROID, SAFARI, SELENESE}, issues = {3771}) */
  @Test
  public void testShouldBeAbleToNavigateForwardsInTheBrowserHistory() {
    driver.get(pages.formPage);

    driver.findElement(By.id("imageButton")).click();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
    Assert.assertEquals(driver.getTitle(), ("We Arrive Here"));

    driver.navigate().back();
    waitFor(pageTitleToBe(driver, "We Leave From Here"));
    Assert.assertEquals(driver.getTitle(), ("We Leave From Here"));

    driver.navigate().forward();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
    Assert.assertEquals(driver.getTitle(), ("We Arrive Here"));
  }

  /*
   * @Ignore(value = {IE, CHROME, SELENESE, IPHONE, OPERA, ANDROID, SAFARI,
   * OPERA_MOBILE}, reason = "Safari: does not support insecure SSL")
   */
  @Test(enabled = false)
  public void testShouldBeAbleToAccessPagesWithAnInsecureSslCertificate() {
    // TODO(user): Set the SSL capability to true.
    //String url = GlobalTestEnvironment.get().getAppServer().whereIsSecure("simpleTest.html");
    //driver.get(url);

    //Assert.assertEquals(driver.getTitle(), ("Hello WebDriver"));
  }

  /*
   * @Ignore({ANDROID, CHROME, HTMLUNIT, IE, IPHONE, OPERA, OPERA_MOBILE,
   * SAFARI, SELENESE})
   */
  @Test(enabled = false)
  public void shouldBeAbleToDisableAcceptOfInsecureSslCertsWithRequiredCapability() {
    // TODO: Resolve why this test doesn't work on the remote server
    /*
     * assumeTrue(TestUtilities.isLocal());
     * 
     * DesiredCapabilities requiredCaps = new DesiredCapabilities();
     * requiredCaps.setCapability(ACCEPT_SSL_CERTS, false); WebDriverBuilder
     * builder = new WebDriverBuilder().setRequiredCapabilities(requiredCaps);
     * localDriver = builder.get();
     * 
     * String url =
     * GlobalTestEnvironment.get().getAppServer().whereIsSecure("simpleTest.html"
     * ); localDriver.get(url);
     * 
     * assertThat(localDriver.getTitle(), not("Hello WebDriver"));
     */
  }

  @Test
  public void testShouldBeAbleToRefreshAPage() {
    driver.get(pages.xhtmlTestPage);

    driver.navigate().refresh();

    Assert.assertEquals(driver.getTitle(), ("XHTML Test Page"));
  }

  /**
   * @throws Exception If the test fails.
   * @see <a href="http://code.google.com/p/selenium/issues/detail?id=208"> Issue 208</a>
   *
   *      This test often causes the subsequent test to fail, in Firefox, on Linux, so we need a new
   *      driver after it.
   * @see <a href="http://code.google.com/p/selenium/issues/detail?id=2282">Issue 2282</a>
   */
  /*
   * @Ignore(value = {IE, SELENESE, IPHONE, OPERA, ANDROID, SAFARI,
   * OPERA_MOBILE}, reason = "Safari: issue 4062; Others: Untested user-agents",
   * issues = {4062})
   * 
   * @NoDriverAfterTest
   * 
   * @JavascriptEnabled
   */
  @Test(enabled = false)
  public void testShouldNotHangIfDocumentOpenCallIsNeverFollowedByDocumentCloseCall()
      throws Exception {
    driver.get(pages.documentWrite);

    // If this command succeeds, then all is well.
    WebElement body = driver.findElement(By.tagName("body"));
    waitFor(WaitingConditions.elementTextToContain(body, "world"));
  }

  @Test(enabled = false)
  public void testShouldNotWaitIndefinitelyIfAnExternalResourceFailsToLoad() {
    String slowPage = appServer.whereIs("slowLoadingResourcePage.html");

    /*
     * Capabilities current = ((HasCapabilities) driver).getCapabilities();
     * DesiredCapabilities caps = new DesiredCapabilities(current);
     * caps.setCapability("webdriver.loading.strategy", "unstable"); WebDriver
     * testDriver = new WebDriverBuilder().setDesiredCapabilities(caps).get();
     */

    long start = System.currentTimeMillis();
    driver.get(slowPage);
    // We discard the element, but want a check to make sure the GET actually
    // completed.
    driver.findElement(By.id("peas"));

    long end = System.currentTimeMillis();
    // The slow loading resource on that page takes 6 seconds to return. If we
    // waited for it, our load time should be over 6 seconds.
    long duration = end - start;

    // driver.quit(); // Clean up before making assertions

    Assert.assertTrue(duration < 5 * 1000, "Took too long to load page: " + duration);
  }

  /*
   * @Ignore(value = {ANDROID, CHROME, HTMLUNIT, IE, IPHONE, OPERA, SAFARI,
   * SELENESE, OPERA_MOBILE}, reason =
   * "Not implemented; Safari: see issue 687, comment 41", issues = {687})
   * 
   * @NeedsLocalEnvironment
   */
  @Test(enabled = false, expectedExceptions = TimeoutException.class)
  public void testShouldTimeoutIfAPageTakesTooLongToLoad() {
    driver.manage().timeouts().pageLoadTimeout(2, SECONDS);

    try {
      // Get the sleeping servlet with a pause of 5 seconds
      String slowPage = appServer.whereIs("sleep?time=5");

      driver.get(slowPage);
      Assert.fail("I should have timed out");
    } finally {
      driver.manage().timeouts().pageLoadTimeout(-1, SECONDS);
    }
  }

}
