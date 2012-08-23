package org.uiautomation.ios.server.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.application.LanguageDictionary;

public class PlistFileUtils {
  
  private final File source;
  
  public PlistFileUtils(File source){
    this.source = source;
  }

  
  public JSONObject toJSON() throws Exception{
    File tmp = File.createTempFile("tmp1234", ".tmp");
    convertL10NFile(tmp);
    JSONObject res = readJSONFile(tmp);
    tmp.delete();
    return res;
  }
  
  
  private void convertL10NFile(File dest) throws IOSAutomationSetupException {
    List<String> c = new ArrayList<String>();
    c.add("plutil");
    c.add("-convert");
    c.add("json");
    c.add("-o");
    c.add(dest.getAbsolutePath());
    c.add(source.getAbsolutePath());
    Command com = new Command(c, true);
    com.executeAndWait();
  }
  
  /**
   * load the content of the file to a JSON object
   * 
   * @param from
   * @return
   * @throws Exception
   */
  private JSONObject readJSONFile(File from) throws Exception {
    FileInputStream is = new FileInputStream(from);
    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, "UTF-8");
    String content = writer.toString();
    content = Normalizer.normalize(content, LanguageDictionary.norme );
    return new JSONObject(content);
  }
}
