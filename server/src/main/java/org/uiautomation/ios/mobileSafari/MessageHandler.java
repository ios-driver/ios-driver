package org.uiautomation.ios.mobileSafari;

import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

public interface MessageHandler {

  public void handle(String msg);
  public JSONObject getResponse(int id) throws TimeoutException;
  public void stop();
}
