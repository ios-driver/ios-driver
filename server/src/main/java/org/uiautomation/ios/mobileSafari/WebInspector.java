package org.uiautomation.ios.mobileSafari;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.VoiceStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchElementException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.server.DOMContext;
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

  private static final Long defaultPageLoadTimeoutInMs = 30000L;

  public RemoteWebElement getDocument() throws Exception {
    DOMContext context = session.getContext().getDOMContext();
    RemoteWebElement result = context.getDocument();
    if (result == null) {
      result = retrieveDocumentAndCheckReady();
      RemoteWebElement window = getMainWindow();
      context.setCurrentFrame(null, result,window);
    }
    return result;
  }

  private RemoteWebElement retrieveDocumentAndCheckReady() {
    RemoteWebElement element;
    while (true) {
      try {
        element = retrieveDocument();
        String state = element.readyState();
        if (element.getNodeId().exist() && ("complete".equals(state) || "loading".equals(state))) {
          if (!"complete".equals(state)){
            System.err.println("retruening a document not yet completed :"+state);
          }
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
    
    enablePageEvent();

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
    cmd.put("params", new JSONObject().put("expression", "window.innerWidth;"));

    JSONObject response = protocol.sendCommand(cmd);
    return cast(response);
  }
  
  public RemoteWebElement getMainWindow() throws JSONException, Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put("params", new JSONObject().put("expression", "window;"));

    JSONObject response = protocol.sendCommand(cmd);
    RemoteObject ro =  cast(response);
    if (ro == null){
      throw new NoSuchElementException("cannot find window");
    }else {
      return ro.getWebElement();
    }
  }

  public Object executeScriptOld(String script, JSONArray args) throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put(
        "params",
        new JSONObject().put("expression", "(function(args) {  alert('args');" + script + "})();")
            .put("arguments", new JSONObject().put("value", "5")).put("returnByValue", false));
    JSONObject response = protocol.sendCommand(cmd);
    return cast(response);
  }

  public Object executeScript(String script, JSONArray args) throws Exception {
    RemoteWebElement document = getDocument();
    RemoteWebElement window = session.getContext().getDOMContext().getWindow();
    JSONObject cmd = new JSONObject();

    script = script.replace("document", "realDocument");
    script = script.replace("window", "realWindow");
    
    
    JSONArray arguments = new JSONArray();
    arguments.put(new JSONObject().put("objectId", window.getRemoteObject().getId()));
    arguments.put(new JSONObject().put("objectId", document.getRemoteObject().getId()));

    cmd.put("method", "Runtime.callFunctionOn");
    cmd.put("params", new JSONObject()
        .put("objectId", document.getRemoteObject().getId())
        .put("functionDeclaration", "(function(realWindow,realDocument) { " + script + "})")
        .put("arguments", arguments)
        .put("returnByValue", false));
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
  
  public String getPageURL() throws Exception {
    RemoteWebElement document = getDocument();
    String f = "(function(arg) { var url=this.URL;return url;})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();

    cmd.put("params", new JSONObject()
      .put("objectId", document.getRemoteObject().getId())
      .put("functionDeclaration", f)
      .put("arguments", args)
      .put("returnByValue", true));

    JSONObject response = getProtocol().sendCommand(cmd);
    return cast(response);
  }
  

  public void get(String url) throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Page.navigate");
    cmd.put("params", new JSONObject().put("url", url));
    JSONObject response = protocol.sendCommand(cmd);
  }
  
  public void enablePageEvent() throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Page.enable");
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
        if (result.has("value") && result.isNull("value")) {
          return null;
        } else if ("array".equals(result.optString("subtype"))) {
          RemoteObject array = new RemoteObject(result.getString("objectId"), session);
          return (T) new RemoteObjectArray(array);
        } else {
          return (T) new RemoteObject(result.getString("objectId"), session);
        }

      } else {
        if ("undefined".equals(type)) {
          return null;
        } else {
          throw new RuntimeException("NI " + response.toString(2));
        }

      }
    } else {
      throw new RuntimeException("bug, null result ");
    }
  }

  public void waitForPageToLoad() {
    long timeout = (Long) session.configure(WebDriverLikeCommand.URL).opt("page load", defaultPageLoadTimeoutInMs);
    if (timeout < 0) {
      timeout = defaultPageLoadTimeoutInMs;
    }
    session.getContext().getDOMContext().waitForPageToLoad(timeout);

  }

  public void back() throws Exception {
    RemoteWebElement document = getDocument();
    String f = "(function(arg) { var f=" + Atoms.back() + "(arg);})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();

    cmd.put("params", new JSONObject().put("objectId", document.getRemoteObject().getId())
        .put("functionDeclaration", f).put("arguments", args).put("returnByValue", true));

    JSONObject response = getProtocol().sendCommand(cmd);
    cast(response);
  }

  public void refresh() throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Page.reload");
    JSONObject response = getProtocol().sendCommand(cmd);
  }

  public void forward() throws Exception {
    RemoteWebElement document = getDocument();
    String f = "(function(arg) { var f=" + Atoms.forward() + "(arg);})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();

    cmd.put("params", new JSONObject().put("objectId", document.getRemoteObject().getId())
        .put("functionDeclaration", f).put("arguments", args).put("returnByValue", true));

    JSONObject response = getProtocol().sendCommand(cmd);
    cast(response);
  }

}
