package org.uiautomation.ios.server.command.web;

import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;

public class FindElement extends BaseCommandHandler{

  public FindElement(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    System.out.println("web find element");
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
   System.out.println("find element and returns it");
   JSONObject element  = new JSONObject().put("ELEMENT","1");
   return new WebDriverLikeResponse(getRequest().getSession(), 0,element);
  }

}
