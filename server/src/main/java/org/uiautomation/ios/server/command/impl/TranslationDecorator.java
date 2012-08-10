package org.uiautomation.ios.server.command.impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class TranslationDecorator implements ResponseDecorator {

  private final IOSApplication aut;

  public TranslationDecorator(SessionsManager instruments) {
    aut = instruments.getCurrentApplication();
  }

  @Override
  public void decorate(WebDriverLikeResponse original) {
    try {
      JSONObject rootNode = ((JSONObject) original.getValue()).getJSONObject("tree");
      addTranslation(rootNode);
    } catch (JSONException e) {
      throw new IOSAutomationException(e.getMessage(), e);
    }
  }


  private void addTranslation(JSONObject node) throws JSONException {

    node.put("l10n", aut.getTranslations(node.getString("name")));
    JSONArray children = node.optJSONArray("children");

    if (children != null && children.length() != 0) {
      for (int i = 0; i < children.length(); i++) {
        JSONObject child = children.getJSONObject(i);
        addTranslation(child);
      }
    }
  }

}
