package org.uiautomation.ios.mobileSafari;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;

public class PlistManager {

  public static final String SET_CONNECTION_KEY = "webinspector/setConnectionKey.xml";
  public static final String CONNECT_TO_APP = "webinspector/connectToApp.xml";
  public static final String SET_SENDER_KEY = "webinspector/setSenderKey.xml";
  public static final String SEND_JSON_COMMAND = "webinspector/sendJSONCommand.xml";



  public String loadFromTemplate(String templatePath) throws IOException {
    InputStream is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
    if (is == null) {
      throw new IOSAutomationSetupException("cannot load : " + templatePath);
    }
    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, "UTF-8");
    String content = writer.toString();
    return content;
  }
  
  public String JSONCommand(JSONObject command) throws IOException{
    String json = command.toString();
    String s = Base64.encodeBase64String(json.getBytes());
    String template = loadFromTemplate(SEND_JSON_COMMAND);
    String res = template.replace("$json_encoded", s);
    return res;
  }

  private byte[] convertPlist(byte[] src, String outputType) throws IOException,
      InterruptedException {
    ProcessBuilder pb =
        new ProcessBuilder("/usr/bin/plutil", "-convert", outputType, "-", "-o", "-");
    Process process = pb.start();
    process.getOutputStream().write(src);
    process.getOutputStream().close();
    int exitCode = process.waitFor();
    //System.err.println("plutil exit code: " + exitCode);
    byte[] bytes = IOUtils.toByteArray(process.getInputStream());
    return bytes;
  }

  public byte[] plistXmlToBinary(String msg) throws IOException, InterruptedException {
    return convertPlist(msg.getBytes(), "binary1");
  }

  public String plistBinaryToXml(byte[] binary) throws IOException, InterruptedException {
    //System.err.println("Converting " + binary.length + " bytes to XML.");
    byte[] bytes = convertPlist(binary, "xml1");
    return new String(bytes);
  }
}
