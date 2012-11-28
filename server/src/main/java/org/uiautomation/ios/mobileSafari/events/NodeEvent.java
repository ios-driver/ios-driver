package org.uiautomation.ios.mobileSafari.events;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.mobileSafari.NodeId;

public class NodeEvent extends RawEvent {

  private static final Logger log = Logger.getLogger(ChildNodeRemoved.class.getName());
  private final NodeId node;
  private final NodeId parent;

  public NodeEvent(JSONObject message) throws JSONException {
    super(message);
    JSONObject params = message.optJSONObject("params");
    if (params == null) {
      log.severe("Cannot parse " + message);
    }
    int parentNodeId = params.getInt("parentNodeId");

    if (params.has("node")) {
      params = params.getJSONObject("node");
    }
    int nodeId = params.getInt("nodeId");

    this.node = new NodeId(nodeId);
    this.parent = new NodeId(parentNodeId);

  }

  public NodeId getNode() {
    return node;
  }

  public NodeId getParent() {
    return parent;
  }

  @Override
  public String toString() {
    return "id:" + node + ",parent;" + parent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((node == null) ? 0 : node.hashCode());
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NodeEvent other = (NodeEvent) obj;
    if (node == null) {
      if (other.node != null)
        return false;
    } else if (!node.equals(other.node))
      return false;
    if (parent == null) {
      if (other.parent != null)
        return false;
    } else if (!parent.equals(other.parent))
      return false;
    return true;
  }
}
