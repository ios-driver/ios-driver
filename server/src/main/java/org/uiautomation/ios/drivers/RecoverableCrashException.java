package org.uiautomation.ios.drivers;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.SessionNotInitializedException;


public class RecoverableCrashException extends WebDriverException {

  public RecoverableCrashException(String msg) {
    super(msg);
  }

  public RecoverableCrashException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
