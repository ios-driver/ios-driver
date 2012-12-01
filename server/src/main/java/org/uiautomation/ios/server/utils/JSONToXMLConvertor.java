package org.uiautomation.ios.server.utils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONToXMLConvertor {

  private final String xml;

  public JSONToXMLConvertor(JSONObject tree) {
    xml = buildXMLDoc(tree).asXML();
  }

  public String asXML() {
    return xml;
  }

  private Document buildXMLDoc(JSONObject tree) {
    Document document = DocumentHelper.createDocument();
    document.setXMLEncoding("UTF-8");
    Element root = document.addElement("root");
    buildXMLNode(tree, root);
    return document;

  }

  private void buildXMLNode(JSONObject from, Element parent) {
    if (from == null) {
      return;
    }
    Element node = parent.addElement(from.optString("type"))
        .addAttribute("name", from.optString("name"))
        .addAttribute("label", from.optString("label"))
        .addAttribute("value", from.optString("value"))
        .addAttribute("ref", from.optString("ref"));

    JSONArray array = from.optJSONArray("children");
    if (array != null) {
      for (int i = 0; i < array.length(); i++) {
        JSONObject n = array.optJSONObject(i);
        buildXMLNode(n, node);
      }
    }
  }
}
