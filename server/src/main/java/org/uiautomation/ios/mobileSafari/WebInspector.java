package org.uiautomation.ios.mobileSafari;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.VoiceStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
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
      context.setCurrentFrame(null, result, window);
    }
    // System.out.println("USING DOCUMENT "+result.getNodeId().getId());
    return result;
  }

  private RemoteWebElement retrieveDocumentAndCheckReady() {
    RemoteWebElement element;
    while (true) {
      try {
        element = retrieveDocument();
        String state = element.readyState();
        if (element.getNodeId().exist() && ("complete".equals(state) || "loading".equals(state))) {
          if (!"complete".equals(state)) {
            System.err.println("retruening a document not yet completed :" + state);
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
    RemoteObject ro = cast(response);
    if (ro == null) {
      throw new NoSuchElementException("cannot find window");
    } else {
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

  // TODO freynaud fix the element swapping.
  public Object executeScript(String script, JSONArray args) throws Exception {
    RemoteWebElement document = getDocument();
    RemoteWebElement window = session.getContext().getDOMContext().getWindow();
    JSONObject cmd = new JSONObject();

    JSONArray arguments = new JSONArray();
    int nbParam = args.length();
    for (int i = 0; i < nbParam; i++) {
      Object arg = args.get(i);
      System.out.println("working on" + arg);
      if (arg instanceof JSONObject) {
        JSONObject jsonArg = (JSONObject) arg;
        if (jsonArg.optInt("ELEMENT") > 0) {
          RemoteWebElement rwep = new RemoteWebElement(new NodeId(jsonArg.optInt("ELEMENT")), session);
          arguments.put(new JSONObject().put("objectId", rwep.getRemoteObject().getId()));
        }
      } else if (arg instanceof JSONArray) {
        JSONArray jsonArr = (JSONArray) arg;
        // maybe by executing some JS returning the array, and using that result
        // as a param ?
        throw new WebDriverException("no support argument = array.");
      } else {
        arguments.put(new JSONObject().put("value", arg));
      }

    }
    arguments.put(new JSONObject().put("objectId", document.getRemoteObject().getId()));
    arguments.put(new JSONObject().put("objectId", window.getRemoteObject().getId()));

    script = script.replace(" document", " arguments[" + nbParam + "]");
    script = script.replace(" window", "  arguments[" + (nbParam + 1) + "]");

    cmd.put("method", "Runtime.callFunctionOn");
    cmd.put(
        "params",
        new JSONObject().put("objectId", document.getRemoteObject().getId())
            .put("functionDeclaration", "(function() { " + script + "})").put("arguments", arguments)
            .put("returnByValue", false));
    JSONObject response = protocol.sendCommand(cmd);
    checkForJSErrors(response);
    Object o = cast(response);
    return o;
  }

  private void checkForJSErrors(JSONObject response) throws Exception {
    // {"result":{"description":"TypeError: 'undefined' is not an object (evaluating 'arguments[0][0].tagName')","objectId":"{\"injectedScriptId\":2,\"id\":7}","className":"Error","type":"object"},"wasThrown":true}
    if (response.optBoolean("wasThrown")) {
      JSONObject details = response.getJSONObject("result");
      String desc = details.optString("description");
      throw new WebDriverException("JS error :" + desc);
    }
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

    cmd.put("params", new JSONObject().put("objectId", document.getRemoteObject().getId())
        .put("functionDeclaration", f).put("arguments", args).put("returnByValue", true));

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

  public <T> T cast(JSONObject response) throws Exception {
    JSONObject body = response.has("result") ? response.getJSONObject("result") : response.getJSONObject("object");

    if (body == null) {
      throw new RuntimeException("Error parsting " + response);
    }

    return cast_(body);

  }

  private <T> T cast_(JSONObject body) throws Exception {
    List<String> primitives = new ArrayList<String>();
    primitives.add("boolean");
    primitives.add("number");
    primitives.add("string");

    String type = body.getString("type");
    // handle null return
    if ("undefined".equals(type)) {
      return (T) null;
    }

    // handle primitive types.
    if (primitives.contains(type)) { // primitive type.
      Object value = body.get("value");
      return (T) value;
    }

    // handle objects
    if ("object".equals(type)) {
      if (body.has("value") && body.isNull("value")) {
        return (T) null;
      }

      if ("array".equals(body.optString("subtype"))) {
        RemoteObject array = new RemoteObject(body.getString("objectId"), session);
        RemoteObjectArray a = new RemoteObjectArray(array);
        ArrayList<Object> res = new ArrayList<Object>();
        for (Object ro : a) {
          res.add(ro);
        }
        return (T) res;
      }

      if (body.has("objectId")) {
        if ("node".equals(body.optString("subtype")) || "Window".equals(body.optString("className"))) {
          return (T) new RemoteObject(body.getString("objectId"), session);
        } else {
          System.out.println("remote object, but not a node." + body);
          RemoteObject ro = new RemoteObject(body.getString("objectId"), session);
          JSONObject o = new JSONObject(ro.stringify());
          return (T) o;
        }

      }
      System.out.println("last type " + body);
      return (T) new RemoteObject(body.getString("objectId"), session);

    }
    throw new RuntimeException("NI " + body);
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
