package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONException;
import org.json.JSONObject;

public class IFrame extends Node {

  private Node contentDocument;
  private String documentURL;


  IFrame(JSONObject o) throws JSONException {
    super(o);
    JSONObject document = o.optJSONObject("contentDocument");
    if (document != null) {
      contentDocument = new Node(document);
    }
  }

  public Node getContentDocument() {
    return contentDocument;
  }


  public String getDocumentURL() {
    return documentURL;
  }
}
