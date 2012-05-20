package org.uiautomation.ios.exceptions;

@SuppressWarnings("serial")
public class InvalidCriteriaException extends IOSAutomationException {

  public InvalidCriteriaException(String message) {
    super(message);
  }

  public InvalidCriteriaException(String message, Throwable t) {
    super(message, t);
  }

}
