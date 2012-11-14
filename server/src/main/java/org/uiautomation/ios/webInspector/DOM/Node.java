package org.uiautomation.ios.webInspector.DOM;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.WebInspector;

public class Node {

  private final JSONObject raw;
  private NodeId nodeId;
  private int childCount;
  private List<Node> children = new ArrayList<Node>();
  private String nodeName;
  private Node contentDocument;
  private String documentURL;
  private int nodeType;
  private final WebInspector inspector;
  private final RemoteObject remoteObject;

  public static Node create(JSONObject o, WebInspector inspector) throws Exception {
    if ("IFRAME".equals(o.optString("nodeName"))) {
      return new IFrame(o, inspector);
    } else {
      return new Node(o, inspector);
    }
  }

  public String getDocumentURL() {
    return documentURL;
  }

  Node(JSONObject o, WebInspector inspector) throws Exception {

    this.raw = o;
    this.inspector = inspector;
    this.nodeId = new NodeId(o.getInt("nodeId"));
    this.nodeName = o.optString("nodeName");
    this.childCount = o.optInt("childCount", 0);

    this.documentURL = o.optString("documentURL");
    this.nodeType = o.optInt("nodeType", 0);

    JSONArray children = o.optJSONArray("children");
    if (children != null) {
      for (int i = 0; i < children.length(); i++) {
        JSONObject child = children.getJSONObject(i);
        this.children.add(new Node(child, inspector));
      }
    }

    RemoteObject ro = null;
    try {
      ro = inspector.resolveNode(nodeId);
    } catch (Exception e) {

      Thread.sleep(2000);
      try {
        ro = inspector.resolveNode(nodeId);
        System.err.println("waiting helped");
      } catch (Exception e1) {
        //System.err.println(e1.getMessage() + "error building node :" + getNodeId() + " -- " + o.toString());
      }
    }
    remoteObject = ro;

  }

  public Node getContentDocument() {
    return contentDocument;
  }

  public NodeId getNodeId() {
    return nodeId;
  }

  public int getChildCount() {
    return childCount;
  }

  public void setChildCount(int childCount) {
    this.childCount = childCount;
  }

  public List<Node> getChildren() {
    return children;
  }

  public void setChildren(List<Node> children) {
    this.children = children;
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public Node getBody() {
    Node res = null;
    for (Node c : getChildren()) {
      if ("BODY".equals(c.getNodeName())) {
        res = c;
        return res;
      }
      res = c.getBody();
      if (res != null) {
        return res;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    try {
      return raw.toString(2);
    } catch (JSONException e) {
      return "ERROR parsing the raw node.";
    }
  }

  public boolean isStale() {
    try {
      inspector.resolveNode(getNodeId());
      return false;
    } catch (Exception e) {
      return true;
    }
  }

  public RemoteObject getRemoteObject() throws Exception {
    return remoteObject;
  }
}
