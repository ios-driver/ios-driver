package org.uiautomation.ios.application;

public class NoSuchLocaleException extends RuntimeException {

  public NoSuchLocaleException(String locale) {
    super("No such locale: " + locale);
  }
}
