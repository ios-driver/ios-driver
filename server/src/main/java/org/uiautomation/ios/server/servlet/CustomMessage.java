/*
 * Copyright 2012 ios-driver committers.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uiautomation.ios.server.servlet;

// TODO freynaud message isn't an exception
public class CustomMessage extends Exception {

  private String msg = null;
  private String type = null;

  public CustomMessage(String msg, String type) {
    super(msg);
    this.msg = msg;
    this.type = type;
  }

  public String getMessage() {
    String final_msg = "";
    if (this.getType().equals("error")) {
      final_msg = this.msg + "<br /> " + this.StackTraceToString();
      return final_msg;
    } else if (this.getType().equals("notice")) {
      final_msg = this.msg;
    }
    return this.msg;
  }

  public String StackTraceToString() {
    StackTraceElement[] stack = this.getStackTrace();
    String stackTo_s = "<p>Stack trace focus in local Classes: </p><ul>";
    for (StackTraceElement i : stack) {
      String[] tokens = i.toString().split("\\.");
      if (tokens.length > 1 && tokens[1].equals("automation")) {
        stackTo_s += "<li>" + i.toString() + "</li><br />";
      }
    }
    return stackTo_s;

  }

  public String getType() {
    return this.type;
  }
}
