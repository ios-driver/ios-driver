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
  
  private File createTmpFile(String xmlContent) throws IOException {
    File res = File.createTempFile("global", ".xml");
    BufferedWriter out = new BufferedWriter(new FileWriter(res));
    out.write(xmlContent);
    out.close();
    return res;
  }

  private File  writeBinaryPListFromXML(String xmlContent) throws IOException {
        File from = createTmpFile(xmlContent);
        File to = File.createTempFile("debug", ".plist");

    List<String> command = new ArrayList<String>();
    command.add("/usr/bin/plutil");
    command.add("-convert");
    command.add("binary1");
    command.add("-o");
    command.add(to.getAbsolutePath());
    command.add(from.getAbsolutePath());

    ProcessBuilder b = new ProcessBuilder(command);
    try {
      Process p = b.start();
      int i = p.waitFor();
      if (i != 0) {
        throw new IOSAutomationException("convertion to binary pfile failed.exitCode=" + i);
      }
    } catch (Exception e) {
      throw new IOSAutomationException("failed to run " + command.toString(), e);
    }
    return to;
  }
  
  private byte[] getBytes(File f) throws FileNotFoundException, IOException{
    return IOUtils.toByteArray(new FileInputStream(f));
  }
  
  

  public byte[] plistXmlToBinary(String msg) throws IOException, InterruptedException {
    //return convertPlist(msg.getBytes(), "binary1");
    File xml = writeBinaryPListFromXML(msg);
    return getBytes(xml);
    
  }

  public String plistBinaryToXml(byte[] binary) throws IOException, InterruptedException {
    //System.err.println("Converting " + binary.length + " bytes to XML.");
    byte[] bytes = convertPlist(binary, "xml1");
    return new String(bytes);
  }
}
