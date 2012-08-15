package org.uiautomation.ios.server.command.impl;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.command.PreHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.command.impl.hack.TimeSpeeder;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class SetTimeoutCommandHandler extends UIAScriptHandler {


  private static final String setTimeout = "UIAutomation.setTimeout(:timeout);"
      + "UIAutomation.createJSONResponse(':sessionId',0,'')";

  public SetTimeoutCommandHandler(SessionsManager context, WebDriverLikeRequest request)
      throws Exception {
    super(context, request);
    addDecorator(new CorrectTimeout(context));
  }

  private String getScript(SessionsManager instruments, WebDriverLikeRequest r) throws Exception {
    int timeout = r.getPayload().getInt("timeout");
    String s = setTimeout.replace(":timeout", String.format("%d", timeout));
    return s;
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    setJS(getScript(getSessionsManager(), getRequest()));
    return super.handle();
  }

  class CorrectTimeout extends PreHandleDecorator {

    public CorrectTimeout(SessionsManager context) {
      super(context);
    }

    @Override
    public void decorate(WebDriverLikeRequest request) {
      try {
        int timeout = request.getPayload().getInt("timeout");
        float timeCorrection = TimeSpeeder.getInstance().getSecondDuration();
        float correctTimeout = timeout * timeCorrection;
        request.getPayload().put("timeout", (int) correctTimeout);
      } catch (Exception e) {
        throw new IOSAutomationException(
            "error correcting the timeout to take the timespeeder into account." + e.getMessage(),
            e);
      }

    }
  }
}
