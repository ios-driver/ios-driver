package org.uiautomation.ios.server;

public class NoSuchLocaleException extends RuntimeException {

  public NoSuchLocaleException(String locale) {
    super("No such locale: " + locale);
  }
}
