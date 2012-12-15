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
