package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIALink;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIAScrollView;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.LocationCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.WebInspector;
import org.uiautomation.ios.server.ServerSideSession;

public class RemoteWebElement extends RemoteObject {

  private final UIADriver nativeDriver;
  private final WebInspector inspector;
  private UIAElement nativeElement;

  public RemoteWebElement(String id, ServerSideSession session) throws JSONException {
    super(id, session);
    this.nativeDriver = session.getNativeDriver();
    this.inspector = session.getWebInspector();
    try {
      getNodeId();
    } catch (Exception e) {
      System.out.println("stale exception");
    }
    System.out.println("RWE ->" + this);
  }

  public void click() throws Exception {
    UIAElement el = getNativeElement();
    String origin = getSession().getWindowHandle();
    try {
      getSession().setNativeContext();
      el.tap();
    } finally {
      getSession().setCurrentContext(origin);
    }

  }

  public NodeId getNodeId() throws JSONException, Exception {
    JSONObject result = inspector.getProtocol().sendCommand(DOM.requestNode(this));
    int id = result.getInt("nodeId");
    NodeId nodeId = new NodeId(id);
    return nodeId;
  }

  public String getText() throws Exception {
    String f = "(function(arg) { " + "var el = this;" + "var regex = /(<([^>]+)>)/ig;" + "var content = el.innerHTML;"
        + "var result = content.replace(regex,'');" + "return result;" + "})";

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", this.getId()));

    cmd.put(
        "params",
        new JSONObject().put("objectId", this.getId()).put("functionDeclaration", f).put("arguments", args)
            .put("returnByValue", true));

    JSONObject response = getProtocol().sendCommand(cmd);
    return getProtocol().cast(response);
  }

  public void highlight() {
    try {
      inspector.highlightNode(getNodeId());
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
      String origin = getSession().getWindowHandle();
      UIARect rect = null;
      try {
        getSession().setNativeContext();
        UIAElement sv = nativeDriver.findElement(new TypeCriteria(UIAScrollView.class));

        // scrollview container. Doesn't start in 0,0
        // x=0,y=96,h=928w=768
        rect = sv.getRect();
      } finally {
        getSession().setCurrentContext(origin);
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
        getSession().setNativeContext();
        // Rect: x=6,y=102,h=14w=94
        // nativeElement = nativeDriver.findElement(new AndCriteria(new
        // TypeCriteria(UIALink.class),new LocationCriteria(x, y)));
        nativeElement = nativeDriver.findElement(new LocationCriteria(x, y));
        // System.out.println(nativeElement + "---" + nativeElement.getRect() +
        // "---"
        // + nativeElement.isVisible() + " --- " + nativeElement.isValid());
      } finally {
        getSession().setCurrentContext(origin);
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
    args.put(new JSONObject().put("value", this.getId()));

    cmd.put(
        "params",
        new JSONObject().put("objectId", this.getId()).put("functionDeclaration", f).put("arguments", args)
            .put("returnByValue", false));

    JSONObject response = getProtocol().sendCommand(cmd);
    return getProtocol().cast(response);
  }

  public String getAttribute(String attributeName) throws Exception {
    return call(".getAttribute('" + attributeName + "')");
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

    RemoteObject document = inspector.getDocument().getRemoteObject();

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("objectId", getId()));
    args.put(new JSONObject().put("objectId", document.getId()));

    cmd.put(
        "params",
        new JSONObject().put("objectId", this.getId()).put("functionDeclaration", f).put("arguments", args)
            .put("returnByValue", true));

    JSONObject response = getProtocol().sendCommand(cmd);
    return getProtocol().cast(response);

  }

  @Override
  public String toString() {
    try {
      return "nodeId=" + getNodeId().getId() + " , remoteElement " + getId();
    } catch (Exception e) {
      return e.getMessage();
    }
  }

}
