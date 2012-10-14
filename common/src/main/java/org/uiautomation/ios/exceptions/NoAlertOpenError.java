package org.uiautomation.ios.exceptions;

@SuppressWarnings("serial")
public class NoAlertOpenError extends IOSAutomationException {
  public NoAlertOpenError(String message) {
    super(message);
  }

  public NoAlertOpenError(String message, Throwable t) {
    super(message, t);
  }
}
