package org.uiautomation.ios.server.command.web;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchFrameException;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
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
    String frameId = getRequest().getPayload().getString("id");
    if ("null".equals(frameId)) {
      getSession().getContext().getDOMContext().setCurrentFrame(null, null,null);
    } else {
      RemoteWebElement iframe = getIframe(frameId);
      RemoteWebElement document = iframe.getContentDocument();
      RemoteWebElement window = iframe.getContentWindow();
      getSession().getContext().getDOMContext().setCurrentFrame(iframe, document,window);
    }

    return new WebDriverLikeResponse(getSession().getSessionId(), 0, new JSONObject());
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

    try {
      int index = Integer.parseInt(id);
      return iframes.get(index);
    } catch (NumberFormatException e) {
      // ignore
    } catch (IndexOutOfBoundsException i) {
      throw new NoSuchFrameException("detected " + iframes.size() + " frames. Cannot get index = " + id);
    }

    throw new NoSuchFrameException("Cannot find frame " + id);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
