package org.uiautomation.ios.selenium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.pageTitleToBe;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class FormHandlingTest extends BaseSeleniumTest {

  @Test
  public void testShouldClickOnSubmitInputElements() {
    driver.get(pages.formPage);
    driver.findElement(By.id("submitButton")).click();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
    assertEquals(driver.getTitle(), ("We Arrive Here"));
  }

  @Test
  public void testClickingOnUnclickableElementsDoesNothing() {
    driver.get(pages.formPage);
    try {
      driver.findElement(By.xpath("//body")).click();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Clicking on the unclickable should be a no-op");
    }
  }

  // @Ignore(value = ANDROID, reason =
  // "The page is zoomed in because of the previous state"
  // + "which causes the click to fail.")
  @Test
  public void testShouldBeAbleToClickImageButtons() {
    driver.get(pages.formPage);
    driver.findElement(By.id("imageButton")).click();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
    assertEquals(driver.getTitle(), ("We Arrive Here"));
  }

  @Test
  public void testShouldBeAbleToSubmitForms() {
    driver.get(pages.formPage);
    driver.findElement(By.name("login")).submit();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
  }

  @Test
  public void testShouldSubmitAFormWhenAnyInputElementWithinThatFormIsSubmitted() {
    driver.get(pages.formPage);
    driver.findElement(By.id("checky")).submit();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
  }

  @Test
  public void testShouldSubmitAFormWhenAnyElementWihinThatFormIsSubmitted() {
    driver.get(pages.formPage);
    driver.findElement(By.xpath("//form/p")).submit();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
  }

  @Test(expectedExceptions = NoSuchElementException.class)
  public void testShouldNotBeAbleToSubmitAFormThatDoesNotExist() {
    driver.get(pages.formPage);

    driver.findElement(By.name("there is no spoon")).submit();
    fail("Should not have succeeded");
  }

  @Test
  // @Ignore(OPERA_MOBILE)
  public void testShouldBeAbleToEnterTextIntoATextAreaBySettingItsValue() {
    driver.get(pages.javascriptPage);
    WebElement textarea = driver.findElement(By.id("keyUpArea"));
    String cheesy = "brie and cheddar";
    textarea.sendKeys(cheesy);
    assertEquals(textarea.getAttribute("value"), cheesy);
    assertEquals(driver.findElement(By.id("result")).getText(), cheesy);
  }

  // @Ignore(value = { ANDROID, OPERA_MOBILE }, reason =
  // "Android: capitalization bug in ICS keeps caps on after a capital letter is sent")
  @Test
  public void testSendKeysKeepsCapitalization() throws InterruptedException {
    driver.get(pages.javascriptPage);
    WebElement textarea = driver.findElement(By.id("keyUpArea"));
    String cheesey = "BrIe And CheDdar";
    textarea.sendKeys(cheesey);
    assertEquals(textarea.getAttribute("value"), (cheesey));
  }

  // @Ignore(value = { SELENESE, IPHONE, ANDROID, OPERA_MOBILE })
  @Test(enabled = false)
  public void testShouldSubmitAFormUsingTheNewlineLiteral() {
    driver.get(pages.formPage);
    WebElement nestedForm = driver.findElement(By.id("nested_form"));
    WebElement input = nestedForm.findElement(By.name("x"));
    input.sendKeys("\n");
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
    assertTrue(driver.getCurrentUrl().endsWith("?x=name"));
  }

  // @Ignore({ SELENESE, IPHONE, ANDROID, OPERA_MOBILE })
  @Test(enabled = false)
  public void testShouldSubmitAFormUsingTheEnterKey() {
    driver.get(pages.formPage);
    WebElement nestedForm = driver.findElement(By.id("nested_form"));
    WebElement input = nestedForm.findElement(By.name("x"));
    input.sendKeys(Keys.ENTER);
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
    assertTrue(driver.getCurrentUrl().endsWith("?x=name"));
  }

  @Test
  public void testShouldEnterDataIntoFormFields() {
    driver.get(pages.xhtmlTestPage);
    WebElement
        element =
        driver.findElement(By.xpath("//form[@name='someForm']/input[@id='username']"));
    String originalValue = element.getAttribute("value");
    assertEquals(originalValue, ("change"));

    element.clear();
    element.sendKeys("some text");

    element = driver.findElement(By.xpath("//form[@name='someForm']/input[@id='username']"));
    String newFormValue = element.getAttribute("value");
    assertEquals(newFormValue, ("some text"));
  }

  // @Ignore(value = { SELENESE, IPHONE, ANDROID, SAFARI, OPERA, OPERA_MOBILE },
  // reason = "Does not yet support file uploads", issues = { 4220 })
  @Test(enabled = false)
  public void testShouldBeAbleToAlterTheContentsOfAFileUploadInputElement() throws IOException {
    driver.get(pages.formPage);
    WebElement uploadElement = driver.findElement(By.id("upload"));
    assertEquals(uploadElement.getAttribute("value"), (""));

    File file = File.createTempFile("test", "txt");
    file.deleteOnExit();

    uploadElement.sendKeys(file.getAbsolutePath());

    String uploadPath = uploadElement.getAttribute("value");
    assertTrue(uploadPath.endsWith(file.getName()));
  }

  // will add a picture in the default "saved picture" album.
  public void addPictures() {
    File
        folder =
        new File(
            "/Users/freynaud/Library/Application Support/iPhone Simulator/6.0/Media/DCIM/100APPLE/");
    File image = new File("/Users/freynaud/image.png");

    File
        plistFolder =
        new File(
            "/Users/freynaud/Library/Application Support/iPhone Simulator/6.0/Media/PhotoData/MISC");
    File plist = new File("/Users/freynaud/DCIM_APPLE.plist");

    try {
      File dest = new File(folder, "image.png");
      FileUtils.copyFile(image, dest);
      dest = new File(plistFolder, "DCIM_APPLE.plist");
      FileUtils.copyFile(plist, dest);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // @Ignore(value = { ANDROID, IPHONE, OPERA, SAFARI, SELENESE, OPERA_MOBILE },
  // reason = "Does not yet support file uploads", issues = { 4220 })
  @Test(enabled = false)
  public void testShouldBeAbleToSendKeysToAFileUploadInputElementInAnXhtmlDocument()
      throws IOException {
    // IE before 9 doesn't handle pages served with an XHTML content type, and
    // just prompts for to
    // download it
    // assumeTrue(!TestUtilities.isInternetExplorer(driver) ||
    // !TestUtilities.isOldIe(driver));

    driver.get(pages.xhtmlFormPage);
    addPictures();
    WebElement uploadElement = driver.findElement(By.id("file"));
    assertEquals(uploadElement.getAttribute("value"), (""));

    File file = File.createTempFile("test", "txt");
    file.deleteOnExit();

    uploadElement.sendKeys(file.getAbsolutePath());

    String uploadPath = uploadElement.getAttribute("value");
    assertTrue(uploadPath.endsWith(file.getName()));
  }

  // @Ignore(value = { SELENESE, IPHONE, ANDROID, OPERA, SAFARI }, reason =
  // "Does not yet support file uploads", issues = { 4220 })
  @Test(enabled = false)
  public void testShouldBeAbleToUploadTheSameFileTwice() throws IOException {
    File file = File.createTempFile("test", "txt");
    file.deleteOnExit();

    driver.get(pages.formPage);
    WebElement uploadElement = driver.findElement(By.id("upload"));
    assertEquals(uploadElement.getAttribute("value"), (""));

    uploadElement.sendKeys(file.getAbsolutePath());
    uploadElement.submit();

    driver.get(pages.formPage);
    uploadElement = driver.findElement(By.id("upload"));
    assertEquals(uploadElement.getAttribute("value"), (""));

    uploadElement.sendKeys(file.getAbsolutePath());
    uploadElement.submit();

    // If we get this far, then we're all good.
  }

  // @Ignore(value = { IPHONE, OPERA }, reason =
  // "iPhone: sendKeys implemented incorrectly.")
  @Test
  // doesn't work if the text is longer than the input size, as the cursor will
  // end up where the tap was done.
  public void testSendingKeyboardEventsShouldAppendTextInInputs() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("working"));
    element.sendKeys("some");
    String value = element.getAttribute("value");
    assertEquals(value, ("some"));

    element.sendKeys(" text");
    value = element.getAttribute("value");
    assertEquals(value, ("some text"));
  }

  @Test
  // doesn't work if the text is longer than the input size, as the cursor will
  // end up where the tap was done.
  public void testShouldScrollWhenZoomed() throws InterruptedException {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.id("email"));
    element.sendKeys("ios@ios.com");
    element = driver.findElement(By.id("vsearchGadget"));
    element.sendKeys("test");


  }

  // @Ignore(value = { ANDROID, IPHONE, OPERA, SELENESE, OPERA_MOBILE }, reason
  // = "iPhone: sendKeys implemented incorrectly. Selenium: just because")
  @Test
  public void testSendingKeyboardEventsShouldAppendTextInInputsWithExistingValue() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("inputWithText"));
    element.sendKeys(". Some text");
    String value = element.getAttribute("value");

    assertEquals(value, ("Example text. Some text"));
  }

  // @Ignore(value = { SELENESE, IPHONE, ANDROID, OPERA_MOBILE }, reason =
  // "Not implemented going to the end of the line first;\n"
  // + "iPhone: sendKeys not implemented correctly")
  @Test
  public void testSendingKeyboardEventsShouldAppendTextinTextAreas() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("withText"));

    element.sendKeys(". Some text");
    String value = element.getAttribute("value");

    assertEquals(value, ("Example text. Some text"));
  }

  @Test
  public void testShouldBeAbleToClearTextFromInputElements() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("working"));
    element.sendKeys("Some text");
    String value = element.getAttribute("value");
    assertTrue(value.length() > 0);

    element.clear();
    value = element.getAttribute("value");

    assertEquals(value.length(), (0));
  }

  @Test
  public void testEmptyTextBoxesShouldReturnAnEmptyStringNotNull() {
    driver.get(pages.formPage);
    WebElement emptyTextBox = driver.findElement(By.id("working"));
    assertEquals(emptyTextBox.getAttribute("value"), "");
  }

  @Test
  public void testShouldBeAbleToClearTextFromTextAreas() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("withText"));
    element.sendKeys("Some text");
    String value = element.getAttribute("value");
    assertTrue(value.length() > 0);

    element.clear();
    value = element.getAttribute("value");

    assertEquals(value.length(), (0));
  }

  @Test(enabled = false)
  // alert not done yet
  // @Ignore(value = { ANDROID, CHROME, HTMLUNIT, IE, IPHONE, OPERA, SAFARI,
  // SELENESE, OPERA_MOBILE }, reason =
  // "Untested on all other browsers, fails on chrome, fails on IE.", issues = {
  // 3508 })
  public void handleFormWithJavascriptAction() {
    String url = appServer.whereIs("form_handling_js_submit.html");
    driver.get(url);
    WebElement element = driver.findElement(By.id("theForm"));
    element.submit();
    Alert alert = driver.switchTo().alert();
    String text = alert.getText();
    alert.dismiss();

    assertEquals("Tasty cheese", text);
  }
}
