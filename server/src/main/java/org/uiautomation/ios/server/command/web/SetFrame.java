package org.uiautomation.ios.server.command.web;

import java.util.List;

import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.Context;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class SetFrame extends BaseCommandHandler{

  public SetFrame(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  // NoSuchWindow - If the currently selected window has been closed.
  // NoSuchFrame - If the frame specified by id cannot be found.
  @Override
  public WebDriverLikeResponse handle() throws Exception {
    String frameId = getRequest().getPayload().getString("id");
    Context c = getSession().getContext();
    
    RemoteWebElement currentDocument = getSession().getWebInspector().getDocument();
    try {
      List<RemoteWebElement> iframes = currentDocument.findElementsByCSSSelector("iframe");
      // string|number|null|WebElement JSON Object
      for (RemoteWebElement iframe : iframes){
        String name = iframe.getAttribute("name");
        if (frameId.equals(name)){
          System.out.println("found the frame !"+name);
          RemoteWebElement document = iframe.getContentDocument();
          getSession().getContext().getDOMContext().setCurrentFrame(iframe, document);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    c.switchToFrame(frameId);
    return new WebDriverLikeResponse(getSession().getSessionId(), 0,new JSONObject());
  }

}
