package org.uiautomation.ios.server.command.impl;

import java.io.File;

import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.instruments.SessionsManager;


public class TakeScreenshot extends DefaultUIAScriptHandler {

  public static final String SCREEN_NAME = "tmpScreenshot";

  public TakeScreenshot(SessionsManager context, WebDriverLikeRequest request) {
    super(context, request);
    addDecorator(new SendBack64EncodedStringDecorator(context));
  }

  class SendBack64EncodedStringDecorator extends PostHandleDecorator {

    public SendBack64EncodedStringDecorator(SessionsManager context) {
      super(context);
    }

    @Override
    public void decorate(WebDriverLikeResponse response) {

      String path = getContext().getCurrentSessionOutputFolder() + "/Run 1/" + SCREEN_NAME +".png";
      File source = new File(path);
      FileTo64EncodedStringUtils encoder = new FileTo64EncodedStringUtils(source);
      try {
        String content64 = encoder.encode();
        source.delete();
        JSONObject value = new JSONObject();
        value.put("64encoded", content64);
        response.setValue(value);
      } catch (Exception e) {
        throw new IOSAutomationException("Error converting " + source.getAbsolutePath()
            + " to a 64 encoded string " + e.getMessage(), e);
      } finally {
        source.delete();
      }

    }
  }
}
