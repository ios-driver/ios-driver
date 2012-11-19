package org.uiautomation.ios.server.command.web;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.UnsupportedCommandException;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class SetFrameHandler extends BaseWebCommandHandler {

  public SetFrameHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  // NoSuchWindow - If the currently selected window has been closed.
  // NoSuchFrame - If the frame specified by id cannot be found.
  @Override
  public WebDriverLikeResponse handle() throws Exception {
    Object p = getRequest().getPayload().get("id");

    if (JSONObject.NULL.equals(p)) {
      getSession().getContext().getDOMContext().setCurrentFrame(null, null, null);
    } else {
      RemoteWebElement iframe;
      if (p instanceof String) {
        iframe = getIframe((String) p);
      } else if (p instanceof Integer) {
        iframe = getIframe((Integer) p);
      } else if (p instanceof JSONObject) {
        String id = ((JSONObject) p).getString("ELEMENT");
        NodeId node = new NodeId(Integer.parseInt(id));
        iframe = new RemoteWebElement(node, getSession());
      } else {
        throw new UnsupportedCommandException("not supported : frame selection by " + p.getClass());
      }

      RemoteWebElement document = iframe.getContentDocument();
      RemoteWebElement window = iframe.getContentWindow();
      getSession().getContext().getDOMContext().setCurrentFrame(iframe, document, window);
    }

    return new WebDriverLikeResponse(getSession().getSessionId(), 0, new JSONObject());
  }

  private RemoteWebElement getIframe(Integer index) throws Exception {
    RemoteWebElement currentDocument = getSession().getWebInspector().getDocument();
    List<RemoteWebElement> iframes = currentDocument.findElementsByCSSSelector("iframe,frame");
    try {
      return iframes.get(index);
    } catch (IndexOutOfBoundsException i) {
      throw new NoSuchFrameException("detected " + iframes.size() + " frames. Cannot get index = " + index);
    }
  }

  private RemoteWebElement getIframe(String id) throws Exception {
    RemoteWebElement currentDocument = getSession().getWebInspector().getDocument();

    List<RemoteWebElement> iframes = currentDocument.findElementsByCSSSelector("iframe,frame");
    // string|number|null|WebElement JSON Object
    for (RemoteWebElement iframe : iframes) {
      String fid = iframe.getAttribute("id");
      if (id.equals(fid)) {
        return iframe;
      }
    }

    for (RemoteWebElement iframe : iframes) {
      String name = iframe.getAttribute("name");
      if (id.equals(name)) {
        return iframe;
      }
    }

    throw new NoSuchFrameException("Cannot find frame " + id);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
