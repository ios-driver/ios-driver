package org.uiautomation.ios.webInspector;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

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


  public DOMContext getCache() {
    return cache;
  }

  public static void main(String[] args) throws Exception {
    WebInspector inspector = new WebInspector();

    Node document = inspector.getDocument();
    inspector.cache.setContextToBase(document);
    
    RemoteObject ro = inspector.resolveNode(document.getNodeId());

    Iterable<RemoteObject> frames = inspector.getAllIFrames();


    RemoteObject input = inspector.findElementById(null, "gh-ac");
    System.out.println(input);
    inspector.highlightNode(inspector.requestNode(input));
    RemoteObject rect = inspector.findPosition(input);
    int top = rect.call(".top");
    System.out.println(top);
    int left = rect.call(".left");
    System.out.println(left);

    /*for (RemoteObject o : frames) {
      NodeId id = inspector.requestNode(o);
      IFrame iframe = inspector.cache.getIFrame(id);
      inspector.cache.setContext(iframe);
      RemoteObject el = inspector.findElementById(null,"srchFrm");
      System.out.println(el);
      RemoteObject found = inspector.findElementById(el,"srchFrm");
      NodeId nodeId = inspector.requestNode(found);
      inspector.highlightNode(nodeId);

    }*/
  }
  
  

  public RemoteObject findPosition(RemoteObject el) throws Exception {
    String f =  "(function(arg) { " +
        "var el = this;"+ 
        "var _x = 0; "+
        "var _y = 0;"+
        
        "while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {"+
        "    _x += el.offsetLeft - el.scrollLeft;"+
        "    _y += el.offsetTop - el.scrollTop;"+
        "    el = el.offsetParent;"+
        "};"+
        "alert('w: '+ this.offsetWidth);"+
        "var res = { top: _y, left: _x , width: this.offsetWidth , height: this.offsetHeight };" +
    	//"alert(res.top +','+res.left);" +
    	"return res;"+
    	"})";
     
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", el.getId()));

    cmd.put(
        "params",
        new JSONObject()
            .put("objectId", el.getId())
            .put("functionDeclaration",f)
            .put("arguments", args).put("returnByValue", false));



    JSONObject response = protocol.sendCommand(cmd);
   return protocol.cast(response);
  }

  public WebInspector() throws UnknownHostException, IOException, InterruptedException {
    cache = new DOMContext();
    MessageHandler handler = new MyMessageHandler(cache,this);
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
  
  public int getInnerWidth() throws JSONException, Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put("params", new JSONObject()
        .put("expression", "window.innerWidth;").put("returnByValue", true));
    JSONObject response = protocol.sendCommand(cmd);
    return protocol.cast(response);
  }
  
  
  

  public RemoteObject findElementById(RemoteObject element,String id) throws Exception {
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", id));

    if (element == null){
      Node document = cache.getCurrentDocument();
      element = resolveNode(document.getNodeId());
    }
    

    cmd.put(
        "params",
        new JSONObject()
            .put("objectId", element.getId())
            .put("functionDeclaration",
                "(function(arg) { var el = this.getElementById(arg);return el;})")
            .put("arguments", args).put("returnByValue", false));

    JSONObject response = protocol.sendCommand(cmd);
    return protocol.cast(response);
  }



}
