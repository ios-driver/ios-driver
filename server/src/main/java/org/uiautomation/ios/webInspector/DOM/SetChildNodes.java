package org.uiautomation.ios.webInspector.DOM;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SetChildNodes {

  private List<Node> nodes;

  public SetChildNodes(JSONObject raw) {
    try {
      nodes = parse(raw);
    } catch (Exception e) {
      throw new IllegalArgumentException("failed parsing the server message", e);
    }
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public List<IFrame> getIFrames() {
    List<IFrame> res = new ArrayList<IFrame>();
    for (Node n : nodes) {
      if (n instanceof IFrame) {
        res.add((IFrame) n);
      }
    }
    return res;
  }


  private List<Node> parse(JSONObject raw) throws Exception {
    List<Node> res = new ArrayList<Node>();
    JSONArray a = raw.getJSONObject("params").getJSONArray("nodes");

    for (int i = 0; i < a.length(); i++) {
      Node nn = Node.create(a.getJSONObject(i));
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
