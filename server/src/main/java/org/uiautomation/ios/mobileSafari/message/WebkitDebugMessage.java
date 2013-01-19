package org.uiautomation.ios.mobileSafari.message;

import org.json.JSONObject;


public class WebkitDebugMessage extends BaseIOSWebKitMessage {

  private final JSONObject content;

  public WebkitDebugMessage(/*Node node*/String rawMessage) throws Exception {
    super(rawMessage);
    content = new JSONObject();
  }


  public JSONObject getMessage() {
    return content;
  }

  @Override
  public String toString() {
    return content.toString();
  }
}
