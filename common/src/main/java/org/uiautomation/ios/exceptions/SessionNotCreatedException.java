package org.uiautomation.ios.exceptions;



@SuppressWarnings("serial")
public class SessionNotCreatedException extends IOSAutomationException {
  public SessionNotCreatedException(String message) {
    super(message);

  }

  public SessionNotCreatedException(String message, Throwable t) {
    super(message, t);

  }
}
