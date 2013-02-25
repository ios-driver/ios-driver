package org.uiautomation.ios.client.uiamodels.impl.augmenter;

import org.json.JSONObject;

import java.io.File;

public interface ElementTree {

  public JSONObject logElementTree(File screenshot, boolean translation);
}
