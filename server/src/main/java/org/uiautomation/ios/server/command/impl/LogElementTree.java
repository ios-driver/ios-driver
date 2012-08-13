package org.uiautomation.ios.server.command.impl;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.impl.decorators.FileTo64EncodedStringUtils;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class LogElementTree extends DefaultUIAScriptHandler {

  public LogElementTree(SessionsManager context, WebDriverLikeRequest request) {
    super(context, request);
    addDecorator(new AttachScreenshotToLog(context));
    try {
      if (request.getPayload().getBoolean("translation")){
        addDecorator(new AddTranslationToLog(context));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
   
  }


  class AddTranslationToLog extends PostHandleDecorator {

    public AddTranslationToLog(SessionsManager context) {
      super(context);
    }

    @Override
    public void decorate(WebDriverLikeResponse response) {
      JSONObject value = (JSONObject) response.getValue();
      try {
        JSONObject rootNode = value.getJSONObject("tree");
        addTranslation(rootNode, getContext().getCurrentApplication());
      } catch (Exception e) {
        e.printStackTrace();
      }


    }

    private void addTranslation(JSONObject node, IOSApplication aut) throws JSONException {

      node.put("l10n", aut.getTranslations(node.getString("name")));
      JSONArray children = node.optJSONArray("children");

      if (children != null && children.length() != 0) {
        for (int i = 0; i < children.length(); i++) {
          JSONObject child = children.getJSONObject(i);
          addTranslation(child, aut);
        }
      }
    }

  }

  class AttachScreenshotToLog extends PostHandleDecorator {

    public AttachScreenshotToLog(SessionsManager context) {
      super(context);
    }

    @Override
    public void decorate(WebDriverLikeResponse response) {
      JSONObject value = (JSONObject) response.getValue();
      boolean screenshot;
      try {
        screenshot = value.getBoolean("screenshot");
      } catch (JSONException e1) {
        screenshot = false;
      }

      if (screenshot) {
        String path =
            getContext().getCurrentSessionOutputFolder() + "/Run 1/" + TakeScreenshot.SCREEN_NAME
                + ".png";
        File source = new File(path);
        FileTo64EncodedStringUtils encoder = new FileTo64EncodedStringUtils(source);
        try {
          String content64 = encoder.encode();
          JSONObject ss = new JSONObject();
          ss.put("64encoded", content64);
          value.put("screenshot", ss);
        } catch (Exception e) {
          throw new IOSAutomationException("Error converting " + source.getAbsolutePath()
              + " to a 64 encoded string " + e.getMessage(), e);
        } finally {
          source.delete();
        }
      } else {
        try {
          value.put("screenshot", JSONObject.NULL);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
