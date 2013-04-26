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
package org.uiautomation.ios.wkrdp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAWebView;
import org.uiautomation.ios.UIAModels.predicate.*;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.context.BaseWebInspector;
import org.uiautomation.ios.server.application.ContentResult;
import org.uiautomation.ios.wkrdp.RemoteExceptionException;
import org.uiautomation.ios.wkrdp.command.DOM;
import org.uiautomation.ios.wkrdp.internal.IosAtoms;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RemoteWebElement {

  private static final Logger log = Logger.getLogger(RemoteWebElement.class.getName());
  private final BaseWebInspector inspector;
  private final NodeId nodeId;
  private RemoteObject remoteObject;

  public RemoteWebElement(NodeId id, BaseWebInspector inspector) {
    if (inspector == null) {
      throw new WebDriverException("inspector cannot be null.");
    }
    this.inspector = inspector;
    this.nodeId = id;
  }

  public RemoteWebElement(NodeId nodeId, RemoteObject remoteObject, BaseWebInspector inspector)
      throws Exception {
    this(nodeId, inspector);
    this.remoteObject = remoteObject;
  }

  public String getReference() {
    return inspector.getPageIdentifier() + "_" + getNodeId().getId();
  }

  public void click() {
    clickAtom();
    inspector.checkForPageLoad();
  }

  // TODO freyanud no return here.
  private void clickAtom() {
    try {
      String f = "(function(arg) { " + "var text = " + IosAtoms.CLICK + "(arg);" + "return text;})";

      JSONArray args = new JSONArray();
      args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

      JSONObject response = getInspectorResponse(f, args, true);
      inspector.cast(response);

    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }

  public void setCursorAtTheEnd() {
    try {
      getRemoteObject().call(
          ".selectionStart=this.value.length;this.selectionEnd=this.value.length;");
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }


  }


  public NodeId getNodeId() {
    return nodeId;
  }

  public boolean exists() {
    try {
      JSONObject response = inspector.sendCommand(DOM.resolveNode(nodeId));
      return true;
    } catch (Exception e) {
      if ("No node with given id found".equals(e.getMessage())) {
        return false;
      }
      throw new RuntimeException("case not implemented" + e.getMessage());
    }

  }

  public RemoteObject getRemoteObject() {
    JSONObject response = null;
    if (remoteObject == null) {
      long start = System.currentTimeMillis();
      try {
        response = inspector.sendCommand(DOM.resolveNode(nodeId));
        RemoteObject o = inspector.cast(response);
        remoteObject = o;
        return remoteObject;
      } catch (RemoteExceptionException e) {
        // Node with given id does not belong to the document
        // No node with given id found
        throw new StaleElementReferenceException(getNodeId() + " is stale." + e.getMessage());
      }
    }
    return remoteObject;
  }

  public String getText() throws Exception {
    String
        f =
        "(function(arg) { " + "var text = " + IosAtoms.GET_VISIBLE_TEXT + "(arg);"
        + "return text;})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    JSONObject response = getInspectorResponse(f, args, true);
    return inspector.cast(response);
  }


  public Point getLocation() throws Exception {
    String
            f =
            "(function(arg) { " + "var loc = " + IosAtoms.GET_LOCATION_IN_VIEW + "(arg);"
                    + "return " + IosAtoms.STRINGIFY + "(loc);})";


    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    JSONObject response = getInspectorResponse(f, args, true);
    String s = inspector.cast(response);
    JSONObject o = new JSONObject(s);
    return new Point(o.getInt("x"), o.getInt("y"));
  }

  public Dimension getSize() throws Exception {
    String
        f =
        "(function(arg) { " + "var size = " + IosAtoms.GET_SIZE + "(arg);"
            + "return " + IosAtoms.STRINGIFY + "(size);})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    JSONObject response = getInspectorResponse(f, args, true);
    String s = inspector.cast(response);
    JSONObject o = new JSONObject(s);
    return new Dimension(o.getInt("width"), o.getInt("height"));

  }

  public void highlight() {
    inspector.highlightNode(nodeId);
  }


  public Point findPosition() throws Exception {

    StringBuilder b = new StringBuilder();
    b.append("(function(){");
    b.append("var element = this;\n");
    b.append("var rect = element.getClientRects()[0];\n");
    b.append("var res = {'x': rect.left, 'y': rect.top};\n");

    b.append(" var doc = element.ownerDocument;\n");
    b.append(" var win = doc.defaultView;\n");
    b.append(" var topWin = win.top;\n");

    b.append(" var parentFrame = function (doc) {\n");
    b.append("    var win = doc.defaultView;\n");
    b.append("    var parentWin = win.parent;\n");
    b.append("    var parentDoc = parentWin.document;\n");
    b.append("    var frames = parentDoc.querySelectorAll('iframe,frame');\n");
    b.append("    for (var i = 0; i < frames.length; i++) {\n");
    b.append("        if (frames[i].contentDocument === doc) {\n");
    b.append("            var r = frames[i];\n");
    b.append("            return r;\n");
    b.append("        }\n");
    b.append("    }\n");
    b.append("    return null;\n");
    b.append("}\n");

    b.append(" while (win !== topWin) {\n");
    b.append("    rect = parentFrame(doc).getClientRects()[0];\n");
    b.append("    var xoff = rect.left;\n");
    b.append("    var yoff = rect.top;\n");
    b.append("    if (xoff > 0) {\n");
    b.append("        res.x += xoff;\n");
    b.append("    }\n");
    b.append("    if (yoff > 0) {\n");
    b.append("        res.y += yoff;\n");
    b.append("    }\n");
    b.append("     win = win.parent;\n");
    b.append("    doc = win.document;\n");
    b.append("}\n");
    b.append("return " + IosAtoms.STRINGIFY + "(res);\n");
    b.append("})");

    JSONObject response = getInspectorResponse(b.toString(), null, false);
    String s = inspector.cast(response);
    JSONObject o = new JSONObject(s);
    return new Point(o.getInt("x"), o.getInt("y"));
  }

  public Point findPositionOld() throws Exception {
    String
        f =
        "(function(a) { " + "var el = this;" + " var rect = el.getClientRects()[0];" + " var res = "
        + IosAtoms.STRINGIFY + "({'top': rect.top,'left': rect.left });" + "return res;" + "})";

    JSONObject response = getInspectorResponse(f, null, false);
    String s = inspector.cast(response);
    JSONObject o = new JSONObject(s);
    return new Point(o.getInt("left"), o.getInt("top"));
  }

  public <T> T getAttribute(String attributeName) {
    T res = getRemoteObject().call("." + attributeName);
    if (res == null || "class".equals(attributeName)) {
      // textarea.value != testarea.getAttribute("value");
      res = getRemoteObject().call(".getAttribute('" + attributeName + "')");
    }
    if (res == null) {
      return (T) "";
    } else {
      return (T) res;
    }
  }

  public String getCssValue(String propertyName) throws Exception {
    String
        f =
        "(function(element,value) { var result = " + IosAtoms.GET_EFFECTIVE_STYLE
        + "(element,value);"
        + "return result;})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));
    args.put(new JSONObject().put("value", propertyName));

    JSONObject response = getInspectorResponse(f, args, true);
    return (String) inspector.cast(response);
  }

  public boolean isSelected() throws Exception {

    String
        f =
        "(function(arg) { " + "var isDisplayed = " + IosAtoms.IS_SELECTED + "(arg);"
        + "return isDisplayed;})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));
    return (Boolean) inspector.cast(getInspectorResponse(f, args, true));
  }

  public boolean isEnabled() throws Exception {

    String f = "(function(arg) { " + "var isEnabled = " + IosAtoms.IS_ENABLED + "(arg);"
               + "return isEnabled;})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));
    return (Boolean) inspector.cast(getInspectorResponse(f, args, true));
  }

  public boolean isDisplayed() throws Exception {
    String
        f =
        "(function(arg) { " + "var isDisplayed = " + IosAtoms.IS_SHOWN + "(arg);"
        + "return isDisplayed;})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));
    return (Boolean) inspector.cast(getInspectorResponse(f, args, true));
  }

  public RemoteWebElement findElementByLinkText(String text, boolean partialMatch)
      throws Exception {

    String ifStatement;
    if (partialMatch) {
      ifStatement = "if ( elements[i].innerText.indexOf(text) != -1 ){";
    } else {
      ifStatement = "if (text === elements[i].innerText ){";
    }
    String f = "(function(text) { " + "var elements = this.querySelectorAll('a');"
               + "for ( var i =0;i<elements.length;i++){" + ifStatement + "  return elements[i];"
               + "}" // end
               // if
               + "}" // end for
               + "return null;" + "})"; // end function

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", text));

    JSONObject response = getInspectorResponse(f, args, false);
    RemoteObject ro = inspector.cast(response);
    if (ro == null) {
      return null;
    } else {
      return ro.getWebElement();
    }
  }

  public List<RemoteWebElement> findElementsByLinkText(String text, boolean partialMatch)
      throws Exception {
    String ifStatement;
    if (partialMatch) {
      ifStatement = "if ( elements[i].innerText.indexOf(text) != -1 ){";
    } else {
      ifStatement = "if (text === elements[i].innerText ){";
    }

    String
        f =
        "(function(text) { " + "var elements = this.querySelectorAll('a');"
        + "var result = new Array();"
        + "for ( var i =0;i<elements.length;i++){" + ifStatement + "  result.push(elements[i]);"
        + "}" // end
        // if
        + "}" // end for
        + "return result;" + "})"; // end function

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", text));

    JSONObject response = getInspectorResponse(f, args, false);
    List<RemoteObject> ros = inspector.cast(response);

    List<RemoteWebElement> res = new ArrayList<RemoteWebElement>();
    for (RemoteObject ro : ros) {
      res.add(ro.getWebElement());
    }
    return res;
  }

  public RemoteWebElement findElementByCSSSelectorOld(String selector) throws Exception {
    try {

      JSONArray args = new JSONArray();
      args.put(new JSONObject().put("value", selector));

      JSONObject
          response =
          getInspectorResponse("(function(arg) { var el = this.querySelector(arg);return el;})",
                               args, false);
      RemoteObject ro = inspector.cast(response);
      if (ro == null) {
        return null;
      } else {
        return ro.getWebElement();
      }
    } catch (Exception e) {
      // e.printStackTrace();
      System.err.println(e.getMessage());
      return null;
    }

  }

  public RemoteWebElement findElementByCSSSelector(String selector) {
    JSONObject response = inspector.sendCommand(DOM.querySelector(nodeId, selector));
    // TODO freynaud
    NodeId id = new NodeId(response.optInt("nodeId"));
    if (!id.exist()) {
      throw new NoSuchElementException("no element matching " + selector);
    }
    RemoteWebElement res = new RemoteWebElement(id, inspector);
    return res;
  }

  public List<RemoteWebElement> findElementsByCSSSelector(String selector) {
    JSONObject response = inspector.sendCommand(DOM.querySelectorAll(nodeId, selector));
    JSONArray nodesId = response.optJSONArray("nodeIds");
    ArrayList<RemoteWebElement> res = new ArrayList<RemoteWebElement>();
    for (int i = 0; i < nodesId.length(); i++) {
      NodeId id = new NodeId(nodesId.optInt(i));
      res.add(new RemoteWebElement(id, inspector));
    }
    return res;

  }

  public String readyState() throws Exception {
    JSONObject
        response =
        getInspectorResponse("(function(arg) { var state = document.readyState; return state;})",
                             null, true);
    return inspector.cast(response);
  }

  @Override
  public String toString() {
    try {
      // String remoteElement = getRemoteObject().getId();
      // String text = getText();
      String remoteElement = remoteObject == null ? "not loaded" : remoteObject.getId();
      return "nodeId=" + nodeId + " , remoteElement " + remoteElement; // +
      // "text:"
      // +
      // text;
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  public RemoteWebElement getContentDocument() throws Exception {

    JSONObject
        response =
        getInspectorResponse(
            "(function(arg) { var document = this.contentDocument; return document;})",
            null, false);
    RemoteObject ro = inspector.cast(response);
    if (ro == null) {
      throw new NoSuchFrameException("Cannot find the document associated with the frame.");
    } else {
      return ro.getWebElement();
    }
  }

  public boolean equalsRemoteWebElement(RemoteWebElement other) throws Exception {

    JSONArray args = new JSONArray();
    String objectId = other.getRemoteObject().getId();
    args.put(new JSONObject().put("objectId", objectId));

    JSONObject response = getInspectorResponse(
        "(function(args) { var me = this; var other=args;alert(me +' -- '+other);return me === other;})",
        args, false);
    boolean equal = (Boolean) inspector.cast(response);
    return equal;

  }

  public void submit() throws Exception {
    String f = "(function(arg) { " + "var text = " + IosAtoms.SUBMIT + "(arg);" + "return text;})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    JSONObject response = getInspectorResponse(f, args, true);
    inspector.cast(response);
    inspector.checkForPageLoad();

  }

  public RemoteWebElement findElementByXpath(String xpath) throws Exception {
    String f = "(function(xpath,element) { var result = " + IosAtoms.XPATH + "(xpath,element);"
               + "return result;})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", xpath));
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    JSONObject response = getInspectorResponse(f, args, false);
    RemoteObject ro = inspector.cast(response);
    if (ro == null) {
      throw new NoSuchElementException("cannot find element by Xpath " + xpath);
    } else {
      return ro.getWebElement();
    }
  }

  public List<RemoteWebElement> findElementsByXpath(String xpath) throws Exception {
    String
        f =
        "(function(xpath,element) { var results = " + IosAtoms.XPATHS + "(xpath,element);"
        + "return results;})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", xpath));
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    JSONObject response = getInspectorResponse(f, args, false);

    List<RemoteObject> ros = inspector.cast(response);

    List<RemoteWebElement> res = new ArrayList<RemoteWebElement>();
    for (RemoteObject ro : ros) {
      res.add(ro.getWebElement());
    }
    return res;
  }

  public void setValueAtoms(String value) throws Exception {
    String
        f =
        "(function(element,value) { var result = " + IosAtoms.TYPE + "(element,value);"
        + "return result;})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));
    args.put(new JSONObject().put("value", value));

    getInspectorResponse(f, args, false);
  }


  public void scrollIntoViewIfNeeded() throws Exception {
    String f = "(function(element) { element.scrollIntoViewIfNeeded()})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    JSONObject response = getInspectorResponse(f, args, true);
    inspector.cast(response);

  }

  public void clear() throws Exception {
    String
        f =
        "(function(element) { " + "var text = " + IosAtoms.CLEAR + "(element);" + "return text;})";

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    JSONObject response = getInspectorResponse(f, args, true);
    inspector.cast(response);

  }

  public RemoteWebElement getContentWindow() throws Exception {

    JSONObject
        response =
        getInspectorResponse("(function(arg) { var window = this.contentWindow; return window;})",
                             new JSONArray(), false);
    RemoteObject ro = inspector.cast(response);
    if (ro == null) {
      throw new NoSuchFrameException("Cannot find the window associated with the frame.");
    } else {
      return ro.getWebElement();
    }
  }

  public String getTagName() {
    String tag = getAttribute("tagName");
    return tag.toLowerCase();
  }

  private JSONObject getInspectorResponse(String javascript, JSONArray args,
                                          Boolean returnByValue) {
    JSONObject cmd = new JSONObject();
    try {
      cmd.put("method", "Runtime.callFunctionOn");

      JSONObject params = new JSONObject().put("objectId", getRemoteObject().getId())
          .put("functionDeclaration", javascript)
          .put("returnByValue", returnByValue);
      if (args != null && args.length() > 0) {
        params.put("arguments", args);
      }
      cmd.put("params", params);

      JSONObject response = inspector.sendCommand(cmd);
      inspector.checkForJSErrors(response);
      return response;
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }

  public BaseWebInspector getInspector() {
    return inspector;
  }
}
