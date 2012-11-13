package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.mobileSafari.WebInspector;

public class IFrame extends Node {

  private Node contentDocument;
  private String documentURL;


  IFrame(JSONObject o,WebInspector inspector) throws JSONException {
    super(o,inspector);
    JSONObject document = o.optJSONObject("contentDocument");
    if (document != null) {
      contentDocument = new Node(document,inspector);
    }
  }

  public Node getContentDocument() {
    return contentDocument;
  }


  public String getDocumentURL() {
    return documentURL;
  }
}
