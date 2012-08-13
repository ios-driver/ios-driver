package org.uiautomation.ios.server.command.impl;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class LogElementTreeDecorated extends DecoratedScriptHandler {

  public LogElementTreeDecorated(final SessionsManager instruments, WebDriverLikeRequest request) {
    super(instruments, request, new ResponseDecorator() {

      @Override
      public void decorate(WebDriverLikeResponse original) {
        try {
          JSONObject value = (JSONObject) original.getValue();
          
          JSONObject rootNode = value.getJSONObject("tree");
          addTranslation(rootNode,instruments);
          
          
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

  



  private static void addTranslation(JSONObject node,final SessionsManager instruments) throws JSONException {

    node.put("l10n", instruments.getCurrentApplication().getTranslations(node.getString("name")));
    JSONArray children = node.optJSONArray("children");

    if (children != null && children.length() != 0) {
      for (int i = 0; i < children.length(); i++) {
        JSONObject child = children.getJSONObject(i);
        addTranslation(child,instruments);
      }
    }
  }
}
