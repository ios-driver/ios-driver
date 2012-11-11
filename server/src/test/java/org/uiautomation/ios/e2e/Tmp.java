package org.uiautomation.ios.e2e;

import org.json.JSONException;
import org.uiautomation.ios.mobileSafari.WebInspector;

public class Tmp {

  public static void main(String[] args) throws JSONException, Exception {
    WebInspector in = new WebInspector(null, "com.yourcompany.UICatalog");
    System.out.println(in.getDocument());
  }


}
