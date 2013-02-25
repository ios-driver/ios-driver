package org.uiautomation.ios.client.uiamodels.impl.augmenter;

import org.uiautomation.ios.communication.WebDriverLikeCommand;

import java.util.Map;

public interface Configurable {

  public void setConfiguration(WebDriverLikeCommand command, String key, Object value);

  public Map<String, Object> getConfiguration(WebDriverLikeCommand command);
}
