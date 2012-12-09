package org.uiautomation.ios.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.IllegalLocatorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.Callable;

import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.pageTitleToBe;

public class ElementFindingTest extends BaseSeleniumTest {


  @Test
  public void testShouldNotBeAbleToLocateASingleElementThatDoesNotExist() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.id("nonExistantButton"));
      Assert.fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldReturnTitleOfPageIfSet() {
    driver.get(pages.xhtmlTestPage);
    Assert.assertEquals(driver.getTitle(), "XHTML Test Page");

    driver.get(pages.simpleTestPage);
    Assert.assertEquals(driver.getTitle(), "Hello WebDriver");
  }

  @Test
  public void testShouldBeAbleToClickOnLinkIdentifiedByText() {
    driver.get(pages.xhtmlTestPage);
    driver.findElement(By.linkText("click me")).click();

    waitFor(pageTitleToBe(driver, "We Arrive Here"));

    Assert.assertEquals(driver.getTitle(), "We Arrive Here");
  }

  @Test
  public void testDriverShouldBeAbleToFindElementsAfterLoadingMoreThanOnePageAtATime() {
    driver.get(pages.formPage);
    driver.get(pages.xhtmlTestPage);
    WebElement
        el =
        driver.findElement(By.cssSelector("div[class='content']>a[href='resultPage.html']"));
    el.click();

    waitFor(pageTitleToBe(driver, "We Arrive Here"));

    Assert.assertEquals(driver.getTitle(), "We Arrive Here");
  }

  @Test
  public void testDriverShouldBeAbleToFindElementsWithImagesOnTop() {
    driver.get(pages.xhtmlTestPage);
    WebElement
        el =
        driver.findElement(By.cssSelector("div[class='content']>a[href='resultPage.html']"));
    el.click();

    waitFor(pageTitleToBe(driver, "We Arrive Here"));

    Assert.assertEquals(driver.getTitle(), "We Arrive Here");
  }

  @Test
  public void testshouldBeAbleToClickOnLinkIdentifiedById() {
    driver.get(pages.xhtmlTestPage);
    driver.findElement(By.id("linkId")).click();

    waitFor(pageTitleToBe(driver, "We Arrive Here"));

    Assert.assertEquals(driver.getTitle(), "We Arrive Here");
  }

  @Test
  public void testShouldThrowAnExceptionWhenThereIsNoLinkToClickAndItIsFoundWithLinkText() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.linkText("Not here either"));
      Assert.fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldfindAnElementBasedOnId() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.id("checky"));

    Assert.assertFalse(element.isSelected());
  }

  @Test
  public void testShouldNotBeAbleTofindElementsBasedOnIdIfTheElementIsNotThere() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.id("notThere"));
      Assert.fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldBeAbleToFindChildrenOfANode() {
    driver.get(pages.selectableItemsPage);
    List<WebElement> elements = driver.findElements(By.xpath("/html/head"));
    WebElement head = elements.get(0);
    List<WebElement> importedScripts = head.findElements(By.tagName("script"));
    Assert.assertEquals(importedScripts.size(), 3);
  }

  @Test
  public void testShouldBeAbleToFindChildrenOfANode2() {
    driver.get(pages.selectableItemsPage);
    List<WebElement> elements = driver.findElements(By.cssSelector("html>head"));
    WebElement head = elements.get(0);
    List<WebElement> importedScripts = head.findElements(By.tagName("script"));
    Assert.assertEquals(importedScripts.size(), 3);
  }

  @Test
  public void testReturnAnEmptyListWhenThereAreNoChildrenOfANode() {
    driver.get(pages.xhtmlTestPage);
    WebElement table = driver.findElement(By.id("table"));
    List<WebElement> rows = table.findElements(By.tagName("tr"));

    Assert.assertEquals(rows.size(), 0);
  }

  @Test
  public void testShouldFindElementsByName() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.name("checky"));

    Assert.assertEquals(element.getAttribute("value"), "furrfu");
  }

  @Test
  public void testShouldFindElementsByClass() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("extraDiv"));
    Assert.assertTrue(element.getText().startsWith("Another div starts here."));
  }

  @Test
  public void testShouldFindElementsByClassWhenItIsTheFirstNameAmongMany() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("nameA"));
    Assert.assertTrue(element.getText().startsWith("An H2 title"));
  }

  @Test
  public void testShouldFindElementsByClassWhenItIsTheLastNameAmongMany() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("nameC"));
    Assert.assertEquals(element.getText(), "An H2 title");
  }

  @Test
  public void testShouldFindElementsByClassWhenItIsInTheMiddleAmongMany() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("nameBnoise"));
    Assert.assertEquals(element.getText(), "An H2 title");
  }

  @Test
  public void testShouldFindElementByClassWhenItsNameIsSurroundedByWhitespace() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("spaceAround"));
    Assert.assertEquals(element.getText(), "Spaced out");
  }

  @Test
  public void testShouldFindElementsByClassWhenItsNameIsSurroundedByWhitespace() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.className("spaceAround"));
    Assert.assertEquals(elements.size(), 1);
    Assert.assertEquals(elements.get(0).getText(), "Spaced out");
  }

  @Test
  public void testShouldNotFindElementsByClassWhenTheNameQueriedIsShorterThanCandidateName() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.className("nameB"));
      Assert.fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsByXPath() {
    driver.get(pages.xhtmlTestPage);
    List<WebElement> elements = driver.findElements(By.xpath("//div"));
    Assert.assertTrue(elements.size() > 1);
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsByLinkText() {
    driver.get(pages.xhtmlTestPage);
    List<WebElement> elements = driver.findElements(By.linkText("click me"));
    Assert.assertEquals(elements.size(), 2);
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsByPartialLinkText() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.partialLinkText("ick me"));

    Assert.assertEquals(elements.size(), 2);
  }

  @Test
  public void testShouldBeAbleToFindElementByPartialLinkText() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.partialLinkText("anon"));
    } catch (NoSuchElementException e) {
      Assert.fail("Expected element to be found");
    }
  }

  @Test
  public void testShouldFindElementByLinkTextContainingEqualsSign() {
    driver.get(pages.xhtmlTestPage);

    try {
      WebElement element = driver.findElement(By.linkText("Link=equalssign"));
      Assert.assertEquals("linkWithEqualsSign", element.getAttribute("id"));
    } catch (NoSuchElementException e) {
      Assert.fail("Expected element to be found");
    }
  }

  @Test
  public void testShouldFindElementByPartialLinkTextContainingEqualsSign() {
    driver.get(pages.xhtmlTestPage);

    try {
      WebElement element = driver.findElement(By.partialLinkText("Link="));
      Assert.assertEquals("linkWithEqualsSign", element.getAttribute("id"));
    } catch (NoSuchElementException e) {
      Assert.fail("Expected element to be found");
    }
  }

  @Test
  public void testShouldFindElementsByLinkTextContainingEqualsSign() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.linkText("Link=equalssign"));
    Assert.assertEquals(1, elements.size());
    Assert.assertEquals("linkWithEqualsSign", elements.get(0).getAttribute("id"));
  }

  @Test
  public void testShouldFindElementsByPartialLinkTextContainingEqualsSign() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.partialLinkText("Link="));
    Assert.assertEquals(1, elements.size());
    Assert.assertEquals("linkWithEqualsSign", elements.get(0).getAttribute("id"));
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsByName() {
    driver.get(pages.nestedPage);

    List<WebElement> elements = driver.findElements(By.name("checky"));

    Assert.assertTrue(elements.size() > 1);
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsById() {
    driver.get(pages.nestedPage);

    List<WebElement> elements = driver.findElements(By.id("2"));

    Assert.assertEquals(8, elements.size());
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsByClassName() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.className("nameC"));

    Assert.assertTrue(elements.size() > 1);
  }

  // You don't want to ask why this is here
  @Test
  public void testWhenFindingByNameShouldNotReturnById() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.name("id-name1"));
    Assert.assertEquals(element.getAttribute("value"), "name");

    element = driver.findElement(By.id("id-name1"));
    Assert.assertEquals(element.getAttribute("value"), "id");

    element = driver.findElement(By.name("id-name2"));
    Assert.assertEquals(element.getAttribute("value"), "name");

    element = driver.findElement(By.id("id-name2"));
    Assert.assertEquals(element.getAttribute("value"), "id");
  }

  @Test
  public void testShouldFindGrandChildren() {
    driver.get(pages.formPage);
    WebElement form = driver.findElement(By.id("nested_form"));
    form.findElement(By.name("x"));
  }

  @Test
  public void testShouldNotFindElementOutSideTree() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.name("login"));
    try {
      element.findElement(By.name("x"));
      Assert.fail("shouldn't find that.");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldReturnElementsThatDoNotSupportTheNameProperty() {
    driver.get(pages.nestedPage);
    driver.findElement(By.name("div1")); // If this works, we're all good
  }

  @Test
  public void testShouldFindHiddenElementsByName() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.name("hidden"));
    } catch (NoSuchElementException e) {
      Assert.fail("Expected to be able to find hidden element");
    }
  }

  @Test
  public void testShouldfindAnElementBasedOnTagName() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.tagName("input"));

    Assert.assertNotNull(element);
  }

  @Test
  public void testShouldfindElementsBasedOnTagName() {
    driver.get(pages.formPage);
    List<WebElement> elements = driver.findElements(By.tagName("input"));

    Assert.assertNotNull(elements);
  }

  @Test
  public void testFindingByCompoundClassNameIsAnError() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.className("a b"));
      Assert.fail("Compound class names aren't allowed");
    } catch (IllegalLocatorException e) {
      // This is expected
    }

    try {
      driver.findElements(By.className("a b"));
      Assert.fail("Compound class names aren't allowed");
    } catch (IllegalLocatorException e) {
      // This is expected
    }
  }

  @Test
  public void testShouldBeAbleToClickOnLinksWithNoHrefAttribute() {
    driver.get(pages.javascriptPage);

    WebElement element = driver.findElement(By.linkText("No href"));
    element.click();

    // if any exception is thrown, we won't get this far. Sanity check
    waitFor(pageTitleToBe(driver, "Changed"));
    Assert.assertEquals("Changed", driver.getTitle());
  }

  @Test
  public void testShouldNotBeAbleToFindAnElementOnABlankPage() {
    driver.get("about:blank");

    try { // Search for anything. This used to cause an IllegalStateException n
      // IE.
      driver.findElement(By.tagName("a"));
      Assert.fail("Should not have been able to find a link");

    } catch (NoSuchElementException e) {
      // this is expected
    }

  }

  // @NeedsFreshDriver
  @Test
  // (enabled=false) // needsFreshDriver not
  public void testShouldNotBeAbleToLocateASingleElementOnABlankPage() {
    // Note we're on the default start page for the browser at this point.

    try {
      driver.findElement(By.id("nonExistantButton"));
      Assert.fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testRemovingAnElementDynamicallyFromTheDomShouldCauseAStaleRefException() {
    driver.get(pages.javascriptPage);

    WebElement toBeDeleted = driver.findElement(By.id("deleted"));
    Assert.assertTrue(toBeDeleted.isDisplayed());

    driver.findElement(By.id("delete")).click();
    boolean wasStale = waitFor(elementToBeStale(toBeDeleted));
    Assert.assertTrue(wasStale, "Element should be stale at this point");
  }

  private Callable<Boolean> elementToBeStale(final WebElement element) {
    return new Callable<Boolean>() {

      public Boolean call() throws Exception {
        try {
          element.isDisplayed();
          return false;
        } catch (StaleElementReferenceException e) {
          return true;
        }
      }
    };
  }

  @Test
  public void testFindingALinkByXpathUsingContainsKeywordShouldWork() {
    driver.get(pages.nestedPage);

    try {
      driver.findElement(By.xpath("//a[contains(.,'hello world')]"));
    } catch (Exception e) {
      Assert.fail("Should not have thrown an exception");
    }
  }

  @Test
  public void testShouldBeAbleToFindAnElementByCssSelector() {
    driver.get(pages.xhtmlTestPage);
    driver.findElement(By.cssSelector("div.content"));
  }

  @Test
  public void testShouldBeAbleToFindElementsByCssSelector() {
    driver.get(pages.xhtmlTestPage);
    driver.findElements(By.cssSelector("p"));
  }

  @Test
  public void testShouldBeAbleToFindAnElementByCompoundCssSelector() {
    driver.get(pages.xhtmlTestPage);
    WebElement element = driver.findElement(By.cssSelector("div.extraDiv, div.content"));
    Assert.assertEquals("content", element.getAttribute("class"));
  }

  @Test
  public void testShouldBeAbleToFindElementsByCompoundCssSelector() {
    driver.get(pages.xhtmlTestPage);
    List<WebElement> elements = driver.findElements(By.cssSelector("div.extraDiv, div.content"));
    Assert.assertEquals("content", elements.get(0).getAttribute("class"));
    Assert.assertEquals("extraDiv", elements.get(1).getAttribute("class"));
  }

  @Test
  public void testFindingByTagNameShouldNotIncludeParentElementIfSameTagType() {
    driver.get(pages.xhtmlTestPage);
    WebElement parent = driver.findElement(By.id("my_span"));

    Assert.assertEquals(2, parent.findElements(By.tagName("div")).size());
    Assert.assertEquals(2, parent.findElements(By.tagName("span")).size());
  }

  @Test
  public void testFindingByCssShouldNotIncludeParentElementIfSameTagType() {
    driver.get(pages.xhtmlTestPage);
    WebElement parent = driver.findElement(By.cssSelector("div#parent"));
    WebElement child = parent.findElement(By.cssSelector("div"));

    Assert.assertEquals("child", child.getAttribute("id"));
  }

  @Test(enabled = false)
  // TODO freynaud : test makes no sense.
  public void testAnElementFoundInADifferentFrameIsStale() {
    driver.get(pages.missedJsReferencePage);
    driver.switchTo().frame("inner");
    WebElement element = driver.findElement(By.id("oneline"));
    driver.switchTo().defaultContent();
    try {
      element.getText();
      Assert.fail("Expected exception");
    } catch (StaleElementReferenceException expected) { // Expected
    }
  }

  @Test
  public void testAnElementFoundInADifferentFrameViaJsCanBeUsed() {
    driver.get(pages.missedJsReferencePage);

    try {
      driver.switchTo().frame("inner");
      WebElement first = driver.findElement(By.id("oneline"));

      driver.switchTo().defaultContent();

      WebElement element = (WebElement) ((JavascriptExecutor) driver)
          .executeScript("return frames[0].document.getElementById('oneline');");
      driver.switchTo().frame("inner");
      WebElement second = driver.findElement(By.id("oneline"));
      Assert.assertEquals(first, element);
      Assert.assertEquals(second, element);
    } finally {
      driver.switchTo().defaultContent();
    }
  }

  @Test
  public void testAnElementFoundInSameFrameViaJsCanBeUsed() {
    driver.get(pages.missedJsReferencePage);

    try {
      driver.switchTo().frame("inner");
      WebElement first = driver.findElement(By.id("oneline"));

      WebElement element = (WebElement) ((JavascriptExecutor) driver)
          .executeScript("return this.getElementById('oneline');");

      WebElement second = driver.findElement(By.id("oneline"));
      Assert.assertEquals(first, element);
      Assert.assertEquals(second, element);
    } finally {
      driver.switchTo().defaultContent();
    }
  }

  @Test
  public void test2() {
    driver.get(pages.missedJsReferencePage);

    try {
      driver.switchTo().frame("inner");
      WebElement first = driver.findElement(By.id("oneline"));
      driver.switchTo().defaultContent();
      first.getText();
      driver.switchTo().frame("inner");
      first.getText();

    } finally {
      driver.switchTo().defaultContent();
    }
  }

  @Test
  public void findsByLinkTextOnXhtmlPage() {
    driver.get(appServer.whereIs("actualXhtmlPage.xhtml"));
    String linkText = "Foo";
    WebElement element = driver.findElement(By.linkText(linkText));
    Assert.assertEquals(linkText, element.getText());
  }

}
