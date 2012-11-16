package org.uiautomation.ios.mobileSafari;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.webInspector.DOM.DOM;
import org.uiautomation.ios.webInspector.DOM.Node;
import org.uiautomation.ios.webInspector.DOM.RemoteObject;
import org.uiautomation.ios.webInspector.DOM.RemoteObjectArray;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class WebInspector {

  private final ServerSideSession session;
  private final DebugProtocol protocol;
  public final UIADriver nativeDriver;
 
  private int width = -1;

  public RemoteWebElement getDocument() throws Exception {
    RemoteWebElement result = session.getContext().getDOMContext().getDocument();
    if (result == null) {
      result = retrieveDocumentAndCheckReady();
    }
    return result;
  }

  private RemoteWebElement retrieveDocumentAndCheckReady() {
    RemoteWebElement element;
    while (true) {
      try {
        element = retrieveDocument();
        if (element.getNodeId().exist() && "complete".equals(element.readyState())) {
          break;
        }
      } catch (Exception e) {
        System.err.println("Workaround in DOMContext, the given document is corrupted, nodeId ");
        // ignore.
      }
    }
    return element;
  }
  
  
 

  private RemoteWebElement retrieveDocument() throws Exception {
    JSONObject result = protocol.sendCommand(DOM.getDocument());
    JSONObject root = result.getJSONObject("root");
    Node res = Node.create(root, this);
    RemoteWebElement rme = new RemoteWebElement(res.getNodeId(), session);
    return rme;
  }

  public RemoteObject findPosition(RemoteObject el) throws Exception {
    String f = "(function(arg) { " + "var el = this;" + "var _x = 0; " + "var _y = 0;" +

    "while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {" + "    _x += el.offsetLeft - el.scrollLeft;"
        + "    _y += el.offsetTop - el.scrollTop;" + "    el = el.offsetParent;" + "};" +
        // "alert('w: '+ this.offsetWidth);"+
        "var res = { top: _y, left: _x , width: this.offsetWidth , height: this.offsetHeight };" +
        // "alert(res.top +','+res.left);" +
        "return res;" + "})";

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", el.getId()));

    cmd.put("params", new JSONObject().put("objectId", el.getId()).put("functionDeclaration", f).put("arguments", args)
        .put("returnByValue", false));

    JSONObject response = protocol.sendCommand(cmd);
    return cast(response);
  }

  public WebInspector(UIADriver nativeDriver, String bundleId, ServerSideSession session) throws Exception {
    this.nativeDriver = nativeDriver;
    this.session = session;
    protocol = new DebugProtocol(session.getContext().getDOMContext(), bundleId, session);

  }

  public DebugProtocol getProtocol() {
    return protocol;
  }

  public int getNativePageWidth() throws Exception {
    if (width == -1) {
      IOSCapabilities caps = nativeDriver.getCapabilities();
      JSONObject json = (JSONObject) caps.getRawCapabilities().get("rect");
      UIARect rect = new UIARect(json);
      width = rect.getWidth();
    }
    return width;

  }

  public RemoteObject resolveNode(NodeId id) throws JSONException, Exception {
    JSONObject result = protocol.sendCommand(DOM.resolveNode(id));
    try {
      JSONObject remoteObject = result.getJSONObject("object");
      // RemoteObject res = new RemoteObject(remoteObject.getString("objectId"),
      // session);
      // return res;
      return null;
    } catch (Exception e) {
      System.err.println("WebInspector.resolveNode() " + result.toString(2));
      return null;
    }

  }

  public void highlightNode(NodeId id) throws JSONException, Exception {
    JSONObject result = protocol.sendCommand(DOM.highlightNode(id));
  }

  public Iterable<RemoteObject> getAllIFrames() throws JSONException, Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put("params",
        new JSONObject().put("expression", "document.getElementsByTagName('iframe');").put("returnByValue", false));
    JSONObject response = protocol.sendCommand(cmd);
    return cast(response);
  }

  public int getInnerWidth() throws JSONException, Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put("params", new JSONObject().put("expression", "window.innerWidth;").put("returnByValue", true));
    JSONObject response = protocol.sendCommand(cmd);
    return cast(response);
  }

  public void stop() {
    protocol.stop();
  }

  public String getPageTitle() throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put("params", new JSONObject().put("expression", "document.title;").put("returnByValue", true));
    JSONObject response = protocol.sendCommand(cmd);
    return cast(response);
  }

 

  public void get(String url) throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Page.navigate");
    cmd.put("params", new JSONObject().put("url", url));
    JSONObject response = protocol.sendCommand(cmd);
  }
  
  public <T> T cast(JSONObject response) throws JSONException {
    List<String> primitives = new ArrayList<String>();
    primitives.add("boolean");
    primitives.add("number");
    primitives.add("string");

    
    // evaluate : 
    
    JSONObject result = response.has("result") ? response.getJSONObject("result") : response.getJSONObject("object");
    
    if (result != null) {
      String type = result.getString("type");


      if (primitives.contains(type)) { // primitive type.
        Object value = result.get("value");
        return (T) value;
      } else if ("object".equals(type)) { // object
        if (result.has("value") && result.isNull("value")){
          return null;
        }else if ("array".equals(result.optString("subtype"))) {
          RemoteObject array = new RemoteObject(result.getString("objectId"), session);
          return (T) new RemoteObjectArray(array);
        } else {
          return (T) new RemoteObject(result.getString("objectId"), session);
        }

      } else {
        throw new RuntimeException("NI " + response.toString(2));
      }
    } else {
      throw new RuntimeException("bug, null result ");
    }
  }

  public void waitForPageToLoad() {
    session.getContext().getDOMContext().waitForPageToLoad();
    
  }


}
