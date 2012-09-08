/*
 * Copyright 2012 ios-driver committers.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uiautomation.ios.client.uiamodels.impl;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIAPoint;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.NoSuchElementException;
import org.uiautomation.ios.exceptions.StaleReferenceException;

/**
 * Main object for all the UIAutomation stuff. Implement part of the Apple API. Some methods are not
 * implemented as the findElement(s) are covering multiple cases.
 * 
 * {@link <a href="http://developer.apple.com/library/ios/#documentation/ToolsLanguages/Reference/UIAElementClassReference/UIAElement/UIAElement.html"> Apple doc</a>
 * for UIAElement }
 */
public class RemoteUIAElement extends RemoteObject implements UIAElement {

  public RemoteUIAElement(RemoteUIADriver driver, String reference) {
    super(driver, reference);
  }


  @Override
  public String getLabel() {
    return getObject(WebDriverLikeCommand.LABEL);
  }

  @Override
  public String getName() {
    return getObject(WebDriverLikeCommand.NAME);
  }

  @Override
  public String getValue() {
    return getObject(WebDriverLikeCommand.VALUE);
  }


  @Override
  public <T> T findElement(Class<T> type, Criteria c) throws NoSuchElementException {
    Criteria newOne = new AndCriteria(new TypeCriteria(type), c);
    return (T) findElement(newOne);
  }



  @Override
  public UIAElement findElement(Criteria c) throws NoSuchElementException {
    try {
      JSONObject payload = new JSONObject();
      payload.put("depth", -1);
      payload.put("criteria", c.getJSONRepresentation());
      return (UIAElement) getRemoteObject(WebDriverLikeCommand.ELEMENT, payload);
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public UIAElementArray<UIAElement> findElements(Criteria c) {
    try {
      JSONObject payload = new JSONObject();
      payload.put("depth", -1);
      payload.put("criteria", c.getJSONRepresentation());
      return (UIAElementArray<UIAElement>) getRemoteObject(WebDriverLikeCommand.ELEMENTS, payload);
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }
  }


  @Override
  public UIAElementArray<UIAElement> getAncestry() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void tap() {
    execute(WebDriverLikeCommand.TAP);
  }

  @Override
  public void touchAndHold(int duration) {
    try {
      JSONObject payload = new JSONObject();
      payload.put("duration", duration);
      execute(WebDriverLikeCommand.TOUCH_AND_HOLD, payload);
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void doubleTap() {
    // TODO Auto-generated method stub

  }

  @Override
  public void twoFingerTap() {
    // TODO Auto-generated method stub

  }

  @Override
  public void scrollToVisible() {
    // TODO Auto-generated method stub

  }

  @Override
  public JSONObject logElementTree(File screenshot, boolean translation) throws Exception {
    WebDriverLikeCommand command = WebDriverLikeCommand.TREE;
    Path p = new Path(command).withSession(getSessionId()).withReference(getReference());
    return RemoteUIAElement.logElementTree(screenshot, translation, p, command, getDriver());
  }


  static JSONObject logElementTree(File screenshot, boolean translation, Path path,
      WebDriverLikeCommand command, RemoteUIADriver driver) {
    try {

      JSONObject payload = new JSONObject();
      if (screenshot == null) {
        payload.put("attachScreenshot", false);
      } else {
        payload.put("attachScreenshot", true);
      }
      payload.put("translation", translation);

      WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), path, payload);
      WebDriverLikeResponse response = driver.execute(request);
      if (response.getValue() == JSONObject.NULL) {
        return null;
      } else {
        Object v = response.getValue();
        if (v instanceof String) {
          return new JSONObject((String) v);
        } else if (v instanceof JSONObject) {
          JSONObject res = (JSONObject) v;
          if (screenshot != null) {
            JSONObject screen = res.getJSONObject("screenshot");
            String content = screen.getString("64encoded");
            RemoteUIATarget.createFileFrom64EncodedString(screenshot, content);
          }
          res.remove("screenshot");
          return res;
        } else {
          throw new IOSAutomationException("can't guess type, got " + v.getClass());
        }
      }
    } catch (Exception e) {
      throw new IOSAutomationException(e);
    }
  }

  // TODO freynaud fix that server side.
  @Override
  public boolean isVisible() {
    Integer i = getObject(WebDriverLikeCommand.IS_VISIBLE);
    if (i == 1) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean isValid() {
    try {
      Boolean stale = getObject(WebDriverLikeCommand.IS_STALE);
      return !stale;
    } catch (StaleReferenceException e) {
      return false;
    }

  }

  @Override
  public UIAPoint getHitPoint() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public UIARect getRect() {
    return getUIARect(WebDriverLikeCommand.RECT);
  }

  @Override
  public UIAElement getParent() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(getClass().getSimpleName());
    builder.append("int. key:" + getReference());
    builder.append(",name:" + getName());
    builder.append(",value:" + getValue());
    builder.append(",label:" + getLabel());
    return builder.toString();
  }



}
