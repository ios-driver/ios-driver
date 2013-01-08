/*
 * Copyright 2012 ios-driver committers.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uiautomation.ios.mobileSafari;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.server.DOMContext;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.webInspector.DOM.DOM;
import org.uiautomation.ios.webInspector.DOM.RemoteObject;
import org.uiautomation.ios.webInspector.DOM.RemoteObjectArray;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WebInspector {

  private static final Logger log = Logger.getLogger(WebInspector.class.getName());

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
    return result;
  }

  private RemoteWebElement retrieveDocumentAndCheckReady() {
    RemoteWebElement element;
    long start = System.currentTimeMillis();
    while (true) {
      try {
        element = retrieveDocument();
        return element;
      } catch (Exception e) {
        log.warning("Workaround in DOMContext, the given document is corrupted, nodeId ");
      }
    }
//    return element;
  }

  private RemoteWebElement retrieveDocument() throws Exception {
    JSONObject result = protocol.sendCommand(DOM.getDocument());
    JSONObject root = result.getJSONObject("root");
    RemoteWebElement rme = new RemoteWebElement(new NodeId(root.getInt("nodeId")), session);
    return rme;
  }

  public RemoteObject findPosition(RemoteObject el) throws Exception {
    String f = "(function(arg) { " + "var el = this;" + "var _x = 0; " + "var _y = 0;" +

               "while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {"
               + "    _x += el.offsetLeft - el.scrollLeft;"
               + "    _y += el.offsetTop - el.scrollTop;" + "    el = el.offsetParent;" + "};" +
               "var res = { top: _y, left: _x , width: this.offsetWidth , height: this.offsetHeight };"
               +
               "return res;" + "})";

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", el.getId()));

    cmd.put("params", new JSONObject().put("objectId", el.getId()).put("functionDeclaration", f)
        .put("arguments", args)
        .put("returnByValue", false));

    JSONObject response = protocol.sendCommand(cmd);
    return cast(response);
  }


  public WebInspector(UIADriver nativeDriver, String bundleId, ServerSideSession session)
      throws Exception {
    this.nativeDriver = nativeDriver;
    this.session = session;
    DOMContext context = session.getContext().getDOMContext();
    protocol =
        new DebugProtocol(context, bundleId/*, new AlertDetector((RemoteUIADriver) nativeDriver)*/);
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
            new JSONObject().put("expression", "document.getElementsByTagName('iframe');")
                .put("returnByValue", false));
    JSONObject response = protocol.sendCommand(cmd);
    return cast(response);
  }

  public int getInnerWidth() throws JSONException, Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put("params", new JSONObject().put("expression", "document.body.clientWidth;"));

    JSONObject response = protocol.sendCommand(cmd);
    return (Integer) cast(response);
  }


  public RemoteWebElement getMainWindow() throws JSONException, Exception {
    /*JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put("params", new JSONObject().put("expression", "window;"));

    JSONObject response = protocol.sendCommand(cmd);
    RemoteObject ro = cast(response);
    if (ro == null) {
      throw new NoSuchElementException("cannot find window");
    } else {
      return ro.getWebElement();
    }*/
    return new RemoteWebElement(new NodeId(0), session);
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
      if (arg instanceof JSONObject) {
        JSONObject jsonArg = (JSONObject) arg;
        if (jsonArg.optInt("ELEMENT") > 0) {
          RemoteWebElement
              rwep =
              new RemoteWebElement(new NodeId(jsonArg.optInt("ELEMENT")), session);
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

    if (!session.getContext().getDOMContext().isOnMainFrame()) {
      arguments.put(new JSONObject().put("objectId", document.getRemoteObject().getId()));
      arguments.put(new JSONObject().put("objectId", window.getRemoteObject().getId()));

      script = script.replace(" document", " arguments[" + nbParam + "]");
      script = script.replace(" window", "  arguments[" + (nbParam + 1) + "]");

    }

    cmd.put("method", "Runtime.callFunctionOn");
    cmd.put(
        "params",
        new JSONObject().put("objectId", document.getRemoteObject().getId())
            .put("functionDeclaration", "(function() { " + script + "})")
            .put("arguments", arguments)
            .put("returnByValue", false));
    JSONObject response = protocol.sendCommand(cmd);
    checkForJSErrors(response);
    Object o = cast(response);
    return o;
  }

  public Dimension getSize() throws Exception {
    String
        f =
        "(function(element) { var result = " + IosAtoms.GET_INTERACTABLE_SIZE + "(window.top);"
        + "var res = " + IosAtoms.STRINGIFY + "(result);"
        + "return  res;  })";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    cmd.put("params",
            new JSONObject()
                .put("objectId", getDocument().getRemoteObject().getId())
                .put("functionDeclaration", f)
                .put("returnByValue", false));

    JSONObject response = protocol.sendCommand(cmd);
    String s = cast(response);
    JSONObject o = new JSONObject(s);
    Dimension dim = new Dimension(o.getInt("width"), o.getInt("height"));
    return dim;

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
    cmd.put("params",
            new JSONObject().put("expression", "document.title;").put("returnByValue", true));
    JSONObject response = protocol.sendCommand(cmd);
    return cast(response);
  }

  public RemoteWebElement findElementByXpath(String xpath) throws Exception {
    String f = "var f=" + IosAtoms.XPATH + ";f('" + xpath + "',document);";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.evaluate");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", xpath));
    cmd.put("params",
            new JSONObject()
                .put("expression", f)
                .put("returnByValue", false));

    JSONObject response = protocol.sendCommand(cmd);

    RemoteObject ro = cast(response);

    return ro.getWebElement();

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
    JSONObject
        body =
        response.has("result") ? response.getJSONObject("result")
                               : response.getJSONObject("object");

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
        if ("node".equals(body.optString("subtype")) || "Window"
            .equals(body.optString("className"))) {
          return (T) new RemoteObject(body.getString("objectId"), session);
        } else {
          RemoteObject ro = new RemoteObject(body.getString("objectId"), session);
          JSONObject o = new JSONObject(ro.stringify());
          return (T) o;
        }

      }
      return (T) new RemoteObject(body.getString("objectId"), session);

    }
    throw new RuntimeException("NI " + body);
  }

  public void waitForPageToLoad() {
    long
        timeout =
        (Long) session.configure(WebDriverLikeCommand.URL)
            .opt("page load", defaultPageLoadTimeoutInMs);
    if (timeout < 0) {
      timeout = defaultPageLoadTimeoutInMs;
    }
    session.getContext().getDOMContext().waitForPageToLoad(timeout);
  }

  public void back() throws Exception {
    RemoteWebElement document = getDocument();
    String f = "(function(arg) { var f=" + IosAtoms.BACK + "(arg);})";
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
    String f = "(function(arg) { var f=" + IosAtoms.FORWARD + "(arg);})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();

    cmd.put("params", new JSONObject().put("objectId", document.getRemoteObject().getId())
        .put("functionDeclaration", f).put("arguments", args).put("returnByValue", true));

    JSONObject response = getProtocol().sendCommand(cmd);
    cast(response);
  }

  public String getHTMLSource() throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put("params", new JSONObject().put("expression", "document.documentElement.outerHTML;")
        .put("returnByValue", true));
    JSONObject response = protocol.sendCommand(cmd);
    return cast(response);
  }

}
