package org.uiautomation.ios.webInspector;

import java.io.IOException;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.webInspector.DOM.DOM;
import org.uiautomation.ios.webInspector.DOM.IFrame;
import org.uiautomation.ios.webInspector.DOM.Node;
import org.uiautomation.ios.webInspector.DOM.RemoteObject;

public class WebInspector {

  private final DebugProtocol protocol;
  private final DOMContext cache;


  public static void main(String[] args) throws Exception {
    WebInspector inspector = new WebInspector();

    Node document = inspector.getDocument();
    RemoteObject ro = inspector.resolveNode(document.getNodeId());

    Iterable<RemoteObject> frames = inspector.getAllIFrames();



    for (RemoteObject o : frames) {
      NodeId id = inspector.requestNode(o);
      IFrame iframe = inspector.cache.getIFrame(id);
      inspector.cache.setContext(iframe);
      RemoteObject el = inspector.findElementById("srchFrm");
      NodeId nodeId = inspector.requestNode(el);
      inspector.highlightNode(nodeId);

    }
  }

  public WebInspector() throws UnknownHostException, IOException, InterruptedException {
    cache = new DOMContext();
    MessageHandler handler = new MyMessageHandler(cache);
    protocol = new DebugProtocol(handler);

  }


  public Node getDocument() throws JSONException, Exception {
    JSONObject result = protocol.sendCommand(DOM.getDocument());
    JSONObject root = result.getJSONObject("root");
    Node res = Node.create(root);
    return res;
  }


  public NodeId requestNode(RemoteObject ro) throws JSONException, Exception {
    JSONObject result = protocol.sendCommand(DOM.requestNode(ro));
    int id = result.getInt("nodeId");
    NodeId nodeId = new NodeId(id);
    return nodeId;
  }

  public RemoteObject resolveNode(NodeId id) throws JSONException, Exception {
    JSONObject result = protocol.sendCommand(DOM.resolveNode(id));
    JSONObject remoteObject = result.getJSONObject("object");
    RemoteObject res = new RemoteObject(remoteObject, protocol);
    return res;
  }

  public void highlightNode(NodeId id) throws JSONException, Exception {
    JSONObject result = protocol.sendCommand(DOM.highlightNode(id));
  }

  public Iterable<RemoteObject> getAllIFrames() throws JSONException, Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put("params", new JSONObject()
        .put("expression", "document.getElementsByTagName('iframe');").put("returnByValue", false));
    JSONObject response = protocol.sendCommand(cmd);
    return protocol.cast(response);
  }

  private RemoteObject findElementById(String id) throws Exception {
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", id));

    Node document = cache.getCurrentDocument();
    RemoteObject remoteDocument = resolveNode(document.getNodeId());

    cmd.put(
        "params",
        new JSONObject()
            .put("objectId", remoteDocument.getId())
            .put("functionDeclaration",
                "(function(arg) { var el = this.getElementById(arg);return el;})")
            .put("arguments", args).put("returnByValue", false));



    JSONObject response = protocol.sendCommand(cmd);
    return protocol.cast(response);
  }



}
