package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONObject;

public class RemoteExceptionException extends RuntimeException {
  private int code;
  private String message;
  private JSONObject command;

  public RemoteExceptionException(int code, String message, JSONObject command) {
    this.code = code;
    this.message = message;
    this.command = command;
  }

  public RemoteExceptionException(JSONObject error, JSONObject command) {
    this.code = error.optInt("code", -1);
    this.message = error.optString("message", "No message for the error.");
    this.command = command;
  }
  
  
  @Override
  public String getMessage() {
    return message;
  }
  
  public int getCode(){
    return code;
  }
}
