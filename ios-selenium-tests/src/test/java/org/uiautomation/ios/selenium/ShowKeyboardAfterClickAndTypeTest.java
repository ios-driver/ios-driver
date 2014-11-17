/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class ShowKeyboardAfterClickAndTypeTest extends BaseSeleniumTest {

  @Test()
  public void testShowKeyBoardAfterClick() throws Exception {
    IOSCapabilities caps = IOSCapabilities.iphone("Safari");
    caps.setCapability(IOSCapabilities.SHOW_KEYBOARD_AFTER_CLICK_AND_TYPE, true);

    startDriver(caps);
    driver.get(pages.formPage);

    String stringyNumber = "";
    String numberNumber = "18003569377";
    String validationNumber = null;
    // iphone has number trackpad, ipad is has full keyboard still
    if (driver.getCapabilities().getCapability(IOSCapabilities.DEVICE).equals("iphone")) {
      validationNumber = numberNumber;
    } else {
      validationNumber = stringyNumber;
    }

    WebElement input = driver.findElement(By.id("telephone"));
    input.sendKeys(numberNumber);
    Thread.sleep(2000);

    assertEquals(input.getAttribute("value"), validationNumber);
    assertTrue(Boolean.TRUE.equals(driver.getRequestedCapabilities().getCapability
            (IOSCapabilities.SHOW_KEYBOARD_AFTER_CLICK_AND_TYPE)));

    stopDriver();
  }

  @Test()
  public void testHideKeyBoardAfterClick() throws Exception {
    driver.get(pages.formPage);

    String stringyNumber = "";
    String numberNumber = "18003569377";
    String validationNumber = null;
    // iphone has number trackpad, ipad is has full keyboard still
    if (driver.getCapabilities().getCapability(IOSCapabilities.DEVICE).equals("iphone")) {
      validationNumber = numberNumber;
    } else {
      validationNumber = stringyNumber;
    }

    WebElement input = driver.findElement(By.id("telephone"));
    input.sendKeys(numberNumber);
    Thread.sleep(2000);

    assertEquals(input.getAttribute("value"), validationNumber);
  }
}
