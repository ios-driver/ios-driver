package org.uiautomation.ios.mobileSafari;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.dd.plist.XMLPropertyListParser;

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

    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
    if (is == null) {
      throw new IOSAutomationSetupException("cannot load : " + templatePath);
    }
    StringWriter writer = new StringWriter();
    try {
      IOUtils.copy(is, writer, "UTF-8");
    } catch (IOException e) {
      throw new IOSAutomationSetupException("Cannot load template " + templatePath);
    }
    String content = writer.toString();
    return content;
  }

  
  
  public String JSONCommand(JSONObject command) throws IOException {
    String json = command.toString();
    String s = Base64.encodeBase64String(json.getBytes());
    String template = loadFromTemplate(SEND_JSON_COMMAND);
    String res = template.replace("$json_encoded", s);
    return res;
  }

  public byte[] plistXmlToBinary(String msg) throws Exception {
    NSObject object = XMLPropertyListParser.parse(msg.getBytes());
    return BinaryPropertyListWriter.writeToArray(object);

  }

  public String plistBinaryToXml(byte[] binary) throws Exception {
    NSObject object = BinaryPropertyListParser.parse(binary);
    return object.toXMLPropertyList();
  }
}
