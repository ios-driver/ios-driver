package org.uiautomation.ios.exceptions;

@SuppressWarnings("serial")
public class UnexpectedAlertOpen extends IOSAutomationException{
  public UnexpectedAlertOpen(String message) {
    super(message);
  }

  public UnexpectedAlertOpen(String message, Throwable t) {
    super(message, t);
  }
}
