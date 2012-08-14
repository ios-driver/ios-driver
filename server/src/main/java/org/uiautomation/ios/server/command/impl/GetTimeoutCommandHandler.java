package org.uiautomation.ios.server.command.impl;

import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.command.impl.session.hack.TimeSpeeder;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class GetTimeoutCommandHandler extends UIAScriptHandler {


  private static final String getTimeout = 
      "var timeout = UIAutomation.TIMEOUT_IN_SEC;"+
      "UIAutomation.createJSONResponse(':sessionId',0,timeout)";

  public GetTimeoutCommandHandler(SessionsManager context, WebDriverLikeRequest request)
      throws Exception {
    super(context, request);
    setJS(getTimeout);
    addDecorator(new CorrectTimeout(context));
  }

  class CorrectTimeout extends PostHandleDecorator {

    public CorrectTimeout(SessionsManager context) {
      super(context);
    }

    @Override
    public void decorate(WebDriverLikeResponse response) {
      try {
        Integer timeout = (Integer) response.getValue();
        float timeCorrection = TimeSpeeder.getInstance().getSecondDuration();
        float correctTimeout = timeout / timeCorrection;
        response.setValue((int)correctTimeout);
      } catch (Exception e) {
        throw new IOSAutomationException(
            "error correcting the timeout to take the timespeeder into account." + e.getMessage(),
            e);
      }


    }

  }

}
