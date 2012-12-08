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
package org.uiautomation.ios.webInspector.DOM;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIAWebView;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.LabelCriteria;
import org.uiautomation.ios.UIAModels.predicate.LocationCriteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.OrCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAKeyboard;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.mobileSafari.Atoms;
import org.uiautomation.ios.mobileSafari.DebugProtocol;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.WebInspector;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.command.uiautomation.GetSessionsNHandler;

public class RemoteWebElement {

  private final UIADriver nativeDriver;
  protected final WebInspector inspector;
  protected final DebugProtocol protocol;
  private UIAElement nativeElement;
  private final ServerSideSession session;
  private final NodeId nodeId;
  private RemoteObject remoteObject;

  public RemoteWebElement(NodeId id, ServerSideSession session) throws JSONException {
    this.session = session;
    this.inspector = session.getWebInspector();
    this.nativeDriver = session.getNativeDriver();
    this.protocol = inspector.getProtocol();
    this.nodeId = id;
  }

  public RemoteWebElement(NodeId nodeId, ServerSideSession session, RemoteObject remoteObject) throws Exception {
    this(nodeId, session);
    this.remoteObject = remoteObject;
  }

  public void click(boolean nativeEvents) throws Exception {
    if (nativeEvents) {
      clickNative();
    } else {
      clickAtom();
    }
  }

  // TODO freyanud no return here.
  private void clickAtom() throws Exception {
    String f = "(function(arg) { " + "var text = " + Atoms.click() + "(arg);" + "return text;})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", true));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    inspector.cast(response);
  }

  private void clickNative() throws Exception {
    UIAElement el = getNativeElement();
    WorkingMode origin = session.getMode();
    try {
      session.setMode(WorkingMode.Native);
      el.tap();
    } finally {
      session.setMode(origin);
    }
  }

  public NodeId getNodeId() {
    return nodeId;
  }

  public boolean exists() {
    try {
      JSONObject response = inspector.getProtocol().sendCommand(DOM.resolveNode(nodeId));
      return true;
    } catch (Exception e) {
      if ("No node with given id found".equals(e.getMessage())) {
        return false;
      }
    }
    throw new RuntimeException("case not implemented");
  }

  public RemoteObject getRemoteObject() throws JSONException, Exception {
    JSONObject response = null;
    if (remoteObject == null) {
      long start = System.currentTimeMillis();
      try {
        response = inspector.getProtocol().sendCommand(DOM.resolveNode(nodeId));
      } catch (RemoteExceptionException e) {
        if ("No node with given id found".equals(e.getMessage())) {
          throw new StaleElementReferenceException(getNodeId() + " is stale.");
        }
      }
      RemoteObject o = inspector.cast(response);
      remoteObject = o;
    }
    return remoteObject;
  }

  public String getText() throws Exception {
    String f = "(function(arg) { " + "var text = " + Atoms.getText() + "(arg);" + "return text;})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", true));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    return inspector.cast(response);
  }

  public void highlight() {
    try {
      inspector.highlightNode(nodeId);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  
  private UIAElement getNativeElement() throws Exception {
    // highlight();
    if (nativeElement == null) {

      // get the web element
      Point po = findPosition();
      int webPageWidth = inspector.getInnerWidth();
      // TODO freynaud use dim, remove innerw
      Dimension dim = inspector.getSize();

      

      WorkingMode origin = session.getMode();

      UIARect rect = null;
      UIARect offset = null;
      try {
        session.setMode(WorkingMode.Native);
        UIAElement sv = nativeDriver.findElement(new TypeCriteria(UIAWebView.class));

        // scrollview container. Doesn't start in 0,0 // x=0,y=96,h=928w=768
        // TODO freynaud : should save the current value, and reset to that at the end. Not to false. 
        nativeDriver.configure(WebDriverLikeCommand.RECT).set("checkForStale", false);
        rect = sv.getRect();

        UIAElement addressBar = nativeDriver.findElement(new AndCriteria(new TypeCriteria(UIAElement.class),
            new NameCriteria("Address",L10NStrategy.serverL10N), new LabelCriteria("Address",L10NStrategy.serverL10N)));
        offset = addressBar.getRect();
        nativeDriver.configure(WebDriverLikeCommand.RECT).set("checkForStale", true);
        // rect = sv.getRect();
      } finally {
        session.setMode(origin);
      }

      int top = po.getY();
      int left = po.getX();

         float ratio = ((float) rect.getWidth()) / ((float) (webPageWidth));

      top = (int) (top * ratio) + 1;
      left = (int) (left * ratio) + 1;

      int statusBarHeigthIphone = 20;
      int x = left;

      int delta = offset.getY() + 39;
      // delta = heigth of the address bar + status bar.
      delta = delta < 20 ? 20 : delta;
      boolean ipad = session.getCapabilities().getDevice() == Device.ipad;
      if (ipad ){
        delta = 96;
      }
      int y = delta + top;
      try {
        session.setMode(WorkingMode.Native);
        nativeElement = nativeDriver.findElement(new LocationCriteria(x, y));
      } finally {
        session.setMode(origin);
      }

    }
    return nativeElement;
  }

  public Point findPosition() throws Exception {
    String f = "(function(a) { " + "var el = this;" + " var rect = el.getClientRects()[0];" + " var res = "
        + Atoms.stringify() + "({'top': rect.top,'left': rect.left });" + "return res;" + "})";

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    cmd.put(
        "params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("returnByValue", false));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    String s = inspector.cast(response);
    JSONObject o = new JSONObject(s);
    return new Point(o.getInt("left"), o.getInt("top"));
    /*
     * String f = "(function(element) { var result = " +
     * Atoms.getLocationInView() + "(element);" + "var res = " +
     * Atoms.stringify() + "(result);" + "return  res;  })"; JSONObject cmd =
     * new JSONObject();
     * 
     * cmd.put("method", "Runtime.callFunctionOn");
     * 
     * JSONArray args = new JSONArray(); args.put(new
     * JSONObject().put("objectId", getRemoteObject().getId()));
     * 
     * cmd.put("params", new JSONObject() .put("objectId",
     * getRemoteObject().getId()) .put("functionDeclaration", f)
     * .put("arguments", args) .put("returnByValue", false));
     * 
     * JSONObject response = inspector.getProtocol().sendCommand(cmd); String
     * res = inspector.cast(response); JSONObject o = new JSONObject(res);
     * return new Point(o.getInt("x"), o.getInt("y"));
     */
  }

  public String getAttribute(String attributeName) throws Exception {
    String res = getRemoteObject().call("." + attributeName);
    if (res == null || "class".equals(attributeName)) {
      // textarea.value != testarea.getAttribute("value");
      res = getRemoteObject().call(".getAttribute('" + attributeName + "')");
    }
    if (res == null) {
      return "";
    } else {
      return res;
    }
  }

  public boolean isSelected() throws Exception {
    String func = Atoms.isSelected();
    String f = "(function(arg) { " + "var isDisplayed = " + func + "(arg);" + "return isDisplayed;})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", true));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    return (Boolean)inspector.cast(response);
  }

  public boolean isDisplayed() throws Exception {
    String func = Atoms.isDisplayed();
    String f = "(function(arg) { " + "var isDisplayed = " + func + "(arg);" + "return isDisplayed;})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", true));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    return (Boolean)inspector.cast(response);
  }

  public RemoteWebElement findElementByLinkText(String text, boolean partialMatch) throws Exception {

    String ifStatement;
    if (partialMatch) {
      ifStatement = "if ( elements[i].innerText.indexOf(text) != -1 ){";
    } else {
      ifStatement = "if (text === elements[i].innerText ){";
    }
    String f = "(function(text) { " + "var elements = this.querySelectorAll('a');"
        + "for ( var i =0;i<elements.length;i++){" + ifStatement + "  return elements[i];" + "}" // end
                                                                                                 // if
        + "}" // end for
        + "return null;" + "})"; // end function

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", text));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", false));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    RemoteObject ro = inspector.cast(response);
    if (ro == null) {
      return null;
    } else {
      return ro.getWebElement();
    }
  }

  public List<RemoteWebElement> findElementsByLinkText(String text, boolean partialMatch) throws Exception {
    String ifStatement;
    if (partialMatch) {
      ifStatement = "if ( elements[i].innerText.indexOf(text) != -1 ){";
    } else {
      ifStatement = "if (text === elements[i].innerText ){";
    }

    String f = "(function(text) { " + "var elements = this.querySelectorAll('a');" + "var result = new Array();"
        + "for ( var i =0;i<elements.length;i++){" + ifStatement + "  result.push(elements[i]);" + "}" // end
                                                                                                       // if
        + "}" // end for
        + "return result;" + "})"; // end function

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", text));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", false));

    JSONObject response = protocol.sendCommand(cmd);
    List<RemoteObject> ros = inspector.cast(response);

    List<RemoteWebElement> res = new ArrayList<RemoteWebElement>();
    for (RemoteObject ro : ros) {
      res.add(ro.getWebElement());
    }
    return res;
  }

  public RemoteWebElement findElementByCSSSelectorOld(String selector) throws Exception {
    try {

      JSONObject cmd = new JSONObject();

      cmd.put("method", "Runtime.callFunctionOn");

      JSONArray args = new JSONArray();
      args.put(new JSONObject().put("value", selector));

      cmd.put(
          "params",
          new JSONObject().put("objectId", getRemoteObject().getId())
              .put("functionDeclaration", "(function(arg) { var el = this.querySelector(arg);return el;})")
              .put("arguments", args).put("returnByValue", false));

      JSONObject response = protocol.sendCommand(cmd);
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

  public RemoteWebElement findElementByCSSSelector(String selector) throws Exception {
    JSONObject response = protocol.sendCommand(DOM.querySelector(nodeId, selector));
    // TODO freynaud
    NodeId id = new NodeId(response.optInt("nodeId"));
    if (!id.exist()) {
      throw new NoSuchElementException("no element matching " + selector);
    }
    RemoteWebElement res = new RemoteWebElement(id, session);
    // RemoteWebElement res = protocol.cast(response);
    return res;
  }

  public List<RemoteWebElement> findElementsByCSSSelector(String selector) throws Exception {
    JSONObject response = protocol.sendCommand(DOM.querySelectorAll(nodeId, selector));
    JSONArray nodesId = response.optJSONArray("nodeIds");
    ArrayList<RemoteWebElement> res = new ArrayList<RemoteWebElement>();
    for (int i = 0; i < nodesId.length(); i++) {
      NodeId id = new NodeId(nodesId.getInt(i));
      res.add(new RemoteWebElement(id, session));
    }
    return res;

  }

  public String readyState() throws Exception {

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    long start = System.currentTimeMillis();
    cmd.put(
        "params",
        new JSONObject().put("objectId", getRemoteObject().getId())
            .put("functionDeclaration", "(function(arg) { var state = document.readyState; return state;})")
            .put("arguments", args).put("returnByValue", true));
    JSONObject response = protocol.sendCommand(cmd);
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

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();

    cmd.put(
        "params",
        new JSONObject().put("objectId", getRemoteObject().getId())
            .put("functionDeclaration", "(function(arg) { var document = this.contentDocument; return document;})")
            .put("arguments", args).put("returnByValue", false));

    JSONObject response = protocol.sendCommand(cmd);
    RemoteObject ro = inspector.cast(response);
    if (ro == null) {
      throw new NoSuchFrameException("Cannot find the document associated with the frame.");
    } else {
      return ro.getWebElement();
    }
  }

  public boolean equalsRemoteWebElement(RemoteWebElement other) throws Exception {

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    String objectId = other.getRemoteObject().getId();
    args.put(new JSONObject().put("objectId", objectId));

    cmd.put(
        "params",
        new JSONObject()
            .put("objectId", getRemoteObject().getId())
            .put("functionDeclaration",
                "(function(args) { var me = this; var other=args;alert(me +' -- '+other);return me === other;})")
            .put("arguments", args).put("returnByValue", false));

    JSONObject response = protocol.sendCommand(cmd);
    boolean equal = (Boolean)inspector.cast(response);
    return equal;

  }

  public void submit() throws Exception {
    String f = "(function(arg) { " + "var text = " + Atoms.submit() + "(arg);" + "return text;})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", true));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    inspector.cast(response);

  }

  public RemoteWebElement findElementByXpath(String xpath) throws Exception {
    String f = "(function(xpath,element) { var result = " + Atoms.findByXpath() + "(xpath,element);"
        + "return result;})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", xpath));
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", false));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    RemoteObject ro = inspector.cast(response);
    if (ro == null) {
      throw new NoSuchElementException("cannot find element by Xpath " + xpath);
    } else {
      return ro.getWebElement();
    }
  }

  public List<RemoteWebElement> findElementsByXpath(String xpath) throws Exception {
    String f = "(function(xpath,element) { var results = " + Atoms.findsByXpath() + "(xpath,element);"
        + "return results;})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", xpath));
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", false));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);

    List<RemoteObject> ros = inspector.cast(response);

    List<RemoteWebElement> res = new ArrayList<RemoteWebElement>();
    for (RemoteObject ro : ros) {
      res.add(ro.getWebElement());
    }
    return res;
  }

  
  public void setValueAtoms(String value) throws Exception {
    String f = "(function(element,value) { var result = " + Atoms.type() + "(element,value);" + "return result;})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));
    args.put(new JSONObject().put("value", value));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", false));

     inspector.getProtocol().sendCommand(cmd);
  }

  public void setValueNative(String value) throws Exception {
    UIAElement el = getNativeElement();
    WorkingMode origin = session.getMode();
    try {
      session.setMode(WorkingMode.Native);
      UIARect rect = el.getRect();
      int x = rect.getX() + rect.getWidth() - 1;
      int y = rect.getY() + rect.getHeight() / 2;
      // TODO freynaud : tap() should take a param like middle, topLeft,
      // bottomRight to save 1 call.
      session.getNativeDriver().tap(x, y);
      RemoteUIAKeyboard keyboard = (RemoteUIAKeyboard) session.getNativeDriver().getKeyboard();
      if ("\n".equals(value)) {
        keyboard.findElement(new NameCriteria("Return")).tap();
      } else {
        keyboard.sendKeys(value);
      }

      Criteria iphone = new NameCriteria("Done");
      Criteria ipad = new NameCriteria("Hide keyboard");

      UIAButton but = session.getNativeDriver().findElement(new OrCriteria(ipad, iphone));
      but.tap();
      // session.getNativeDriver().pinchClose(300, 400, 50, 100, 1);
    } finally {
      session.setMode(origin);
    }

  }

  public void clear() throws Exception {
    String f = "(function(element) { " + "var text = " + Atoms.clear() + "(element);" + "return text;})";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", true));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    inspector.cast(response);

  }

  public RemoteWebElement getContentWindow() throws Exception {
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();

    cmd.put(
        "params",
        new JSONObject().put("objectId", getRemoteObject().getId())
            .put("functionDeclaration", "(function(arg) { var window = this.contentWindow; return window;})")
            .put("arguments", args).put("returnByValue", false));

    JSONObject response = protocol.sendCommand(cmd);
    RemoteObject ro = inspector.cast(response);
    if (ro == null) {
      throw new NoSuchFrameException("Cannot find the window associated with the frame.");
    } else {
      return ro.getWebElement();
    }
  }

  public String getTagName() throws Exception {
    String tag = getAttribute("tagName");
    return tag.toLowerCase();
  }

}
