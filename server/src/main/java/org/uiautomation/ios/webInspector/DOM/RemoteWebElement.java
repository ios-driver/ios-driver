package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIAScrollView;
import org.uiautomation.ios.UIAModels.predicate.LocationCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.webInspector.DebugProtocol;
import org.uiautomation.ios.webInspector.WebInspector;

public class RemoteWebElement extends RemoteObject {

  private final UIADriver nativeDriver;
  private final WebInspector inspector;
  private UIAElement nativeElement;
  private UIAElement sv;

  public RemoteWebElement(JSONObject raw, DebugProtocol protocol, UIADriver nativeDriver,
      WebInspector inspector) throws JSONException {
    super(raw, protocol);
    this.nativeDriver = nativeDriver;
    this.inspector = inspector;
  }


  public void click() throws Exception {
    UIAElement el = getNativeElement();
    el.tap();
  }


  private UIAElement getNativeElement() throws Exception {
    if (nativeElement == null) {
      sv = nativeDriver.findElement(new TypeCriteria(UIAScrollView.class));
      // scrollview container. Doesn't start in 0,0
      UIARect rect = sv.getRect();
      RemoteObject ro = findPosition();

      int webPageWidth = inspector.getInnerWidth();
      int nativePageWith = inspector.getNativePageWidth();

      float ratio = ((float) nativePageWith) / ((float) (webPageWidth));
      int top = ro.call(".top");
      int left = ro.call(".left");

      top = (int) (top * ratio) + 1;
      left = (int) (left * ratio) + 1;

      int x = rect.getX() + left;
      int y = rect.getY() + top;
      
      nativeElement = nativeDriver.findElement(new LocationCriteria(x, y));
    }
    return nativeElement;
  }



  public RemoteObject findPosition() throws Exception {
    String f =
        "(function(arg) { "
            + "var el = this;"
            + "var _x = 0; "
            + "var _y = 0;"
            +

            "while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {"
            + "    _x += el.offsetLeft - el.scrollLeft;"
            + "    _y += el.offsetTop - el.scrollTop;"
            + "    el = el.offsetParent;"
            + "};"
            + "var res = { top: _y, left: _x , width: this.offsetWidth , height: this.offsetHeight };"
            + "return res;" + "})";

    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", this.getId()));

    cmd.put("params", new JSONObject().put("objectId", this.getId()).put("functionDeclaration", f)
        .put("arguments", args).put("returnByValue", false));



    JSONObject response = getProtocol().sendCommand(cmd);
    return getProtocol().cast(response);
  }

}
