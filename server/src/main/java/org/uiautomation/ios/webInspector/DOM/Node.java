package org.uiautomation.ios.webInspector.DOM;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Node {

  private int id;
  private int childCount;
  private List<Node> children = new ArrayList<Node>();
  private String nodeName;


  public Node(JSONObject o) throws JSONException {
    this.id = o.getInt("nodeId");
    this.nodeName = o.optString("nodeName");
    this.childCount = o.optInt("childCount",0);

    JSONArray children = o.optJSONArray("children");
    if (children != null) {
      for (int i = 0; i < children.length(); i++) {
        JSONObject child = children.getJSONObject(i);
        this.children.add(new Node(child));
      }
    }
  }


  public int getId() {
    return id;
  }


  public void setId(int id) {
    this.id = id;
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
  
  public Node getBody(){
    Node res = null;
    for (Node c :  getChildren()){
      if ("BODY".equals(c.getNodeName())){
        res = c;
        return res;
      }
      res = c.getBody();
      if (res !=null){
        return res;
      }
    }
    return null;
  }
  
  @Override
  public String toString() {
    return "node : "+getId();
  }
}
