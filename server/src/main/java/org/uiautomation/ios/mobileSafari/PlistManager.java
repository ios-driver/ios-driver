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
package org.uiautomation.ios.mobileSafari;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSObject;
import com.dd.plist.XMLPropertyListParser;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.webInspector.DOM.Runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

public class PlistManager {

  public static final String SET_CONNECTION_KEY = "webinspector/setConnectionKey.xml";
  public static final String CONNECT_TO_APP = "webinspector/connectToApp.xml";
  public static final String SET_SENDER_KEY = "webinspector/setSenderKey.xml";
  public static final String SEND_JSON_COMMAND = "webinspector/sendJSONCommand.xml";

  private static String cacheTemplate = loadFromTemplate(SEND_JSON_COMMAND);

  public static String loadFromTemplate(String templatePath) {
    if (SEND_JSON_COMMAND.equals(templatePath) && cacheTemplate != null) {
      return cacheTemplate;
    }

    InputStream
        is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
    if (is == null) {
      throw new WebDriverException("cannot load : " + templatePath);
    }
    StringWriter writer = new StringWriter();
    try {
      IOUtils.copy(is, writer, "UTF-8");
    } catch (IOException e) {
      throw new WebDriverException("Cannot load template " + templatePath);
    }
    String content = writer.toString();
    return content;
  }


  public String JSONCommand(JSONObject command) {
    String json = command.toString();
    /*try {
      json = "{\"method\":\"Runtime.evaluate\",\"params\":{\"expression\":\"alert('command "+command.getInt("id")+"')\",\"objectGroup\":\"console\",\"includeCommandLineAPI\":true,\"doNotPauseOnExceptionsAndMuteConsole\":true,\"returnByValue\":false},\"id\":"+command.getInt("id")+"}";
    } catch (JSONException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }*/
    String s = null;
    try {
      s = Base64.encodeBase64String(json.getBytes("UTF-8"));
      System.out.println("encoded :" + s);
      //s =
      //    "eyJtZXRob2QiOiJSdW50aW1lLmV2YWx1YXRlIiwicGFyYW1zIjp7ImV4cHJlc3Npb24iOiJhbGVydCgndHQxMjMnKSIsIm9iamVjdEdyb3VwIjoiY29uc29sZSIsImluY2x1ZGVDb21tYW5kTGluZUFQSSI6dHJ1ZSwiZG9Ob3RQYXVzZU9uRXhjZXB0aW9uc0FuZE11dGVDb25zb2xlIjp0cnVlLCJyZXR1cm5CeVZhbHVlIjpmYWxzZX0sImlkIjo1N30=";
      String decoded = new String(Base64.decodeBase64(s.getBytes("UTF-8")));
      System.out.println(decoded);


    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    String template = loadFromTemplate(SEND_JSON_COMMAND);
    String res = template.replace("$json_encoded", s);
    return res;
  }

  public byte[] plistXmlToBinary(String msg) {
    NSObject object = null;
    try {
      object = XMLPropertyListParser.parse(msg.getBytes());
      return BinaryPropertyListWriter.writeToArray(object);
    } catch (Exception e) {
      throw new WebDriverException(e);
    }


  }

  public String plistBinaryToXml(byte[] binary) throws Exception {
    try {
      NSObject object = BinaryPropertyListParser.parse(binary);
      return object.toXMLPropertyList();
    } catch (Exception e) {
      return null;
    }
  }
}
