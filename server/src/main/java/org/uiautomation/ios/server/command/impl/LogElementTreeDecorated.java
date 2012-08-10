package org.uiautomation.ios.server.command.impl;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class LogElementTreeDecorated extends DecoratedScriptHandler {

  public LogElementTreeDecorated(final SessionsManager instruments, WebDriverLikeRequest request) {
    super(instruments, request, new ResponseDecorator() {

      @Override
      public void decorate(WebDriverLikeResponse original) {
        try {
          JSONObject value = (JSONObject) original.getValue();
          boolean screenshot = value.getBoolean("screenshot");
          if (screenshot) {
            String path = instruments.getCurrentSessionOutputFolder() + "/Run 1/tmpScreenshot.png";
            File tmp = PostTargetScreenshotWithName.waitForFileToAppearOnDisk(path);

            JSONObject ss = new JSONObject();
            ss.put("64encoded", PostTargetScreenshotWithName.to64encodedString(tmp));
            tmp.delete();
            value.put("screenshot", ss);
          }
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }


      }
    });
  }

}
