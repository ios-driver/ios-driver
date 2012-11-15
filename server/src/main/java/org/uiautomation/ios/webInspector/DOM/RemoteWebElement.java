package org.uiautomation.ios.webInspector.DOM;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIALink;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIAScrollView;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.LocationCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.mobileSafari.DebugProtocol;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.WebInspector;
import org.uiautomation.ios.server.ServerSideSession;

public class RemoteWebElement {

  private final UIADriver nativeDriver;
  private final WebInspector inspector;
  private final DebugProtocol protocol;
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

  public void click() throws Exception {
    UIAElement el = getNativeElement();
    String origin = session.getWindowHandle();
    try {
      session.setNativeContext();
      el.tap();
    } finally {
      session.setCurrentContext(origin);
    }
  }

  public NodeId getNodeId() {
    return nodeId;
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
      RemoteObject o = inspector.getProtocol().cast(response);
      remoteObject = o;
      System.err.println("getting remote object ofr node ID:" + nodeId + "\t" + (System.currentTimeMillis() - start)
          + ".ms");
    }
    return remoteObject;
  }

  public String getText() throws Exception {
    String f = "(function(arg) { " + "var el = this;" + "var regex = /(<([^>]+)>)/ig;" + "var content = el.innerHTML;"
        + "var result = content.replace(regex,'');" + "return result;" + "})";

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", true));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    return inspector.getProtocol().cast(response);
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
      String origin = session.getWindowHandle();
      UIARect rect = null;
      try {
        session.setNativeContext();
        UIAElement sv = nativeDriver.findElement(new TypeCriteria(UIAScrollView.class));

        // scrollview container. Doesn't start in 0,0
        // x=0,y=96,h=928w=768
        rect = sv.getRect();
      } finally {
        session.setCurrentContext(origin);
      }

      // get the web element
      RemoteObject ro = findPosition();

      int webPageWidth = inspector.getInnerWidth();

      int top = ro.call(".top");
      int left = ro.call(".left");

      // resize to account for the ios resizing of the page
      /*
       * int nativePageWith = 0; try { getSession().setNativeContext();
       * nativePageWith = inspector.getNativePageWidth();
       * 
       * } finally { getSession().setCurrentContext(origin); }
       */

      float ratio = ((float) rect.getWidth()) / ((float) (webPageWidth));

      top = (int) (top * ratio) + 1;
      left = (int) (left * ratio) + 1;

      int x = rect.getX() + left;
      int y = rect.getY() + top;
      // System.out.println("looking for the element at : " + x + "," + y);
      // find the corresponding native element
      try {
        session.setNativeContext();
        // Rect: x=6,y=102,h=14w=94
        // nativeElement = nativeDriver.findElement(new AndCriteria(new
        // TypeCriteria(UIALink.class),new LocationCriteria(x, y)));
        nativeElement = nativeDriver.findElement(new LocationCriteria(x, y));
        // System.out.println(nativeElement + "---" + nativeElement.getRect() +
        // "---"
        // + nativeElement.isVisible() + " --- " + nativeElement.isValid());
      } finally {
        session.setCurrentContext(origin);
      }

    }
    return nativeElement;
  }

  public RemoteObject findPosition() throws Exception {
    String f = "(function(arg) { " + "var el = this;" + "var _x = 0; " + "var _y = 0;" +

    "while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {" + "    _x += el.offsetLeft - el.scrollLeft;"
        + "    _y += el.offsetTop - el.scrollTop;" + "    el = el.offsetParent;" + "};"
        + "var res = { top: _y, left: _x , width: this.offsetWidth , height: this.offsetHeight };" + "return res;"
        + "})";

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", getRemoteObject().getId()));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", false));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    return inspector.getProtocol().cast(response);
  }

  public String getAttribute(String attributeName) throws Exception {
    return getRemoteObject().call(".getAttribute('" + attributeName + "')");
  }

  public boolean isSelected() throws Exception {
    String checked = getAttribute("checked");
    return "checked".equals(checked);
  }

  /**
   * doesn't check for dispaly= none, just that there is no other div above it
   * and that's in the viewport
   * 
   * @return
   * @throws Exception
   */
  public boolean isDisplayed() throws Exception {
    highlight();
    String f = "(function(element,document) {"
        + "  if (element.offsetWidth === 0 || element.offsetHeight === 0){ return false;}"
        + "  var height = document.documentElement.clientHeight;" + "  var rects = element.getClientRects();"
        + "  var on_top = function(r) {"
        + "        var x = (r.left + r.right)/2, y = (r.top + r.bottom)/2;"
        + "        var isOnTop = document.elementFromPoint(x, y) === element;"
        // +"alert(x+' -- '+isOnTop);"
        + "        return isOnTop; " + "      };" + "  for (var i = 0, l = rects.length; i < l; i++) {"
        + "    var r = rects[i];"
        // +"alert(r.top>0+' -- '+r.top <= height+'---'+r.bottom > 0+'--'+r.bottom <= height);"
        + "    var in_viewport = r.top > 0 ? r.top <= height : (r.bottom > 0 && r.bottom <= height);"
        + "    if (in_viewport && on_top(r))" + " {return true;}" + "  }" + "  return false;" + "})";

    RemoteWebElement document = inspector.getCache().getCurrentDocument();

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getRemoteObject().getId()));
    args.put(new JSONObject().put("objectId", document.getRemoteObject().getId()));

    cmd.put("params",
        new JSONObject().put("objectId", getRemoteObject().getId()).put("functionDeclaration", f)
            .put("arguments", args).put("returnByValue", true));

    JSONObject response = inspector.getProtocol().sendCommand(cmd);
    return inspector.getProtocol().cast(response);

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
    RemoteObject ro = inspector.getProtocol().cast(response);
    if (ro == null) {
      return null;
    } else {
      return ro.getWebElement();
    }
  }

  public List<RemoteWebElement> findElementsByLinkText(String text, boolean partialMatch) throws Exception {
    Node document = null;

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
    RemoteObjectArray ra = protocol.cast(response);

    List<RemoteWebElement> res = new ArrayList<RemoteWebElement>();
    if (ra != null) {
      for (RemoteObject ro : ra) {
        res.add(ro.getWebElement());
      }

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
      RemoteObject ro = protocol.cast(response);
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
    // TODO freynaud
    return res;

  }

  public String readyState() throws Exception {

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();

    cmd.put(
        "params",
        new JSONObject().put("objectId", getRemoteObject().getId())
            .put("functionDeclaration", "(function(arg) { var state = document.readyState; return state;})")
            .put("arguments", args).put("returnByValue", true));

    JSONObject response = protocol.sendCommand(cmd);
    return protocol.cast(response);
  }

  @Override
  public String toString() {
    try {
      //String remoteElement = getRemoteObject().getId();
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

}
