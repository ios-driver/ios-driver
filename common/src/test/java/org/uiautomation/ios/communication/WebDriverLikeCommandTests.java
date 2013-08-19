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

package org.uiautomation.ios.communication;

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;


public class WebDriverLikeCommandTests {

  @Test
  public void newSession() {
    String method = "POST";
    String path = "/session";
    Assert.assertEquals(WebDriverLikeCommand.getCommand(method, path),
                        WebDriverLikeCommand.NEW_SESSION);
  }


  @Test
  public void deleteSession() {
    String method = "DELETE";
    String path = "/session/ab-258";
    Assert.assertEquals(WebDriverLikeCommand.getCommand(method, path),
                        WebDriverLikeCommand.DELETE_SESSION);
  }


  @Test
  public void localTarget() {
    String method = "GET";
    String path = "/session/ab-258/url";
    Assert.assertEquals(WebDriverLikeCommand.getCommand(method, path),
                        WebDriverLikeCommand.CURRENT_URL);
  }


  @Test(expectedExceptions = WebDriverException.class)
  public void localTargetNeg1() {
    String method = "POST";
    String path = "/session/ab-258/localTarget";
    Assert.assertEquals(WebDriverLikeCommand.getCommand(method, path), WebDriverLikeCommand.URL);
  }

  @Test(expectedExceptions = WebDriverException.class)
  public void localTargetNeg2() {
    String method = "GET";
    String path = "/session/ab-258/url/ferret";
    Assert.assertEquals(WebDriverLikeCommand.getCommand(method, path), WebDriverLikeCommand.URL);
  }
}
