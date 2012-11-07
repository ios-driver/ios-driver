package org.uiautomation.ios.webInspector.DOM;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetChildNodes {

  private List<Node> nodes;

  public SetChildNodes(JSONObject raw) {
    try {
      nodes = parse(raw);
    } catch (JSONException e) {
      throw new IllegalArgumentException("failed parsing the server message", e);
    }
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public List<Node> getIFrames() {
    List<Node> res = new ArrayList<Node>();
    for (Node n : nodes) {
      if ("IFRAME".equals(n.getNodeName())) {
        res.add(n);
      }
    }
    return res;
  }


  private List<Node> parse(JSONObject raw) throws JSONException {
    List<Node> res = new ArrayList<Node>();
    JSONArray a = raw.getJSONObject("params").getJSONArray("nodes");

    for (int i = 0; i < a.length(); i++) {
      Node nn = new Node(a.getJSONObject(i));
      res.add(nn);
      if (nn.getContentDocument() != null) {
        res.add(nn.getContentDocument());
      }
    }
    return res;
  }


  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();

    for (Node n : nodes) {
      b.append("node : " + n.toString());
    }
    return b.toString();
  }
}
