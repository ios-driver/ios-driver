package org.uiautomation.ios.ide.pages.begin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class IDETestHelper {


  public static void persist(File screenshot, JSONObject tree, File dest)
      throws FileNotFoundException, IOException, JSONException {
    System.out.println(screenshot.getAbsolutePath());
    FileWriter write = new FileWriter(dest);
    IOUtils.copy(new StringReader(tree.toString(2)), new FileOutputStream(dest), "UTF-8");
    System.out.println(dest.getAbsolutePath());
  }

}
