/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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
package org.uiautomation.ios.communication;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.JsonToBeanConverter;
import org.openqa.selenium.remote.Response;

import java.io.InputStream;
import java.io.StringWriter;

public class Helper {

  // Webdriver exception have debug info for the client. polluting server side.
  public static String extractMessage(Throwable e) {
    String msg = e.getMessage();
    return msg == null ? "" : (e instanceof WebDriverException) ? msg.split("\n")[0] : msg;
  }

  public static JSONObject extractObject(HttpResponse resp) {
    String str = "";
    try {
      InputStream is = resp.getEntity().getContent();
      StringWriter writer = new StringWriter();
      IOUtils.copy(is, writer, "UTF-8");

      str = writer.toString();
      return new JSONObject(str);
    } catch (Exception e) {
      throw new WebDriverException(str, e);
    }
  }

  public static String extractString(HttpResponse resp) {
    String str = "";
    try {
      InputStream is = resp.getEntity().getContent();
      StringWriter writer = new StringWriter();
      IOUtils.copy(is, writer, "UTF-8");

      str = writer.toString();
      return str;
    } catch (Exception e) {
      throw new WebDriverException(str, e);
    }
  }

  public static Response exctractResponse(HttpResponse resp) {
    String s = extractString(resp);
    try {
      Response response = new JsonToBeanConverter().convert(Response.class, s);
      return response;
    } catch (ClassCastException cce) {
      throw new WebDriverException("not a valid response " + s);
    }

  }

}
