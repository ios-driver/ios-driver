package org.uiautomation.ios.application;

public class NoSuchLanguageCodeException extends RuntimeException {

  public NoSuchLanguageCodeException(String languageCode) {
    super("No such language code: " + languageCode);
  }
}
