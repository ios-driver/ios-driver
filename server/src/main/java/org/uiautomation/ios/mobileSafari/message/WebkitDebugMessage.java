package org.uiautomation.ios.mobileSafari.message;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Node;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;


public class WebkitDebugMessage extends BaseIOSWebKitMessage {

  private final JSONObject content;

  public WebkitDebugMessage(/*Node node*/String rawMessage) throws Exception {
    super(rawMessage);
    /*String encoded = node.getText();
    byte[] bytes = Base64.decodeBase64(encoded);
    String s = new String(bytes);
    try {
      content = new JSONObject(s);
    } catch (JSONException e) {
      throw new WebDriverException("Error extracting the webkit message", e);
    } */
    content = new JSONObject();
  }


  public JSONObject getMessage() {
    return content;
  }
}
