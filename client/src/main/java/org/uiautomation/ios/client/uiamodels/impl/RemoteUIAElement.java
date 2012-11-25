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
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAPoint;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.NoSuchElementException;

import com.google.common.collect.ImmutableMap;

/**
 * Main object for all the UIAutomation stuff. Implement part of the Apple API.
 * Some methods are not implemented as the findElement(s) are covering multiple
 * cases.
 * 
 * {@link <a href="http://developer.apple.com/library/ios/#documentation/ToolsLanguages/Reference/UIAElementClassReference/UIAElement/UIAElement.html"> Apple doc</a>
 * for UIAElement }
 */
public class RemoteUIAElement extends RemoteIOSObject implements UIAElement {

  public RemoteUIAElement(RemoteUIADriver driver, String reference) {
    super(driver, reference);
  }

  @Override
  public String getLabel() {
    return getAttribute("label");
  }

  @Override
  public String getName() {
    return getAttribute("name");
  }

  @Override
  public String getValue() {
    return getAttribute("value");
  }

  @Override
  public <T extends UIAElement> T findElement(Class<T> type, Criteria c) throws NoSuchElementException {
    Criteria newOne = new AndCriteria(new TypeCriteria(type), c);
    return (T) findElement(newOne);
  }

  @Override
  public <T extends UIAElement> T findElement(Criteria c) throws NoSuchElementException {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENT,
        ImmutableMap.of("depth", -1, "criteria", c.stringify()));
    return getDriver().execute(request);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<UIAElement> findElements(Criteria c) {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENTS,
        ImmutableMap.of("depth", -1, "criteria", c.stringify()));
    return getDriver().execute(request);
  }

  @Override
  protected WebElement findElement(String by, String using) {
    if (using == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null.");
    }

    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENT,
        ImmutableMap.of("using", by, "value", using));
    return getDriver().execute(request);

  }

  @Override
  protected List<WebElement> findElements(String by, String using) {
    if (using == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null.");
    }

    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENTS,
        ImmutableMap.of("using", by, "value", using));
    return getDriver().execute(request);
  }

  protected WebDriverLikeRequest buildRequest(WebDriverLikeCommand command, Map<String, ?> params) {
    return getDriver().buildRequest(command, this, params);
  }

  @Override
  public void tap() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.CLICK);
    getDriver().execute(request);
  }

  protected WebDriverLikeRequest buildRequest(WebDriverLikeCommand command) {
    return buildRequest(command, null);
  }

  @Override
  public void touchAndHold(int duration) {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.TOUCH_AND_HOLD,
        ImmutableMap.of("duration", duration));
    getDriver().execute(request);

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
    Path p = new Path(command).withSession(getDriver().getSessionId()).withReference(getReference());
    return RemoteUIAElement.logElementTree(screenshot, translation, p, command, getDriver());
  }

  static JSONObject logElementTree(File screenshot, boolean translation, Path path, WebDriverLikeCommand command,
      RemoteUIADriver driver) {
    try {

      JSONObject payload = new JSONObject();
      if (screenshot == null) {
        payload.put("attachScreenshot", false);
      } else {
        payload.put("attachScreenshot", true);
      }
      payload.put("translation", translation);

      WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), path, payload);
      Response response = driver.execute(request);
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
            RemoteUIADriver.createFileFrom64EncodedString(screenshot, content);
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
  public boolean isDisplayed() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.DISPLAYED);
    return getDriver().execute(request);
  }

  @Override
  public UIARect getRect() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.RECT);
    Map<String, Object> rect = getDriver().execute(request);
    Map<String, Long> origin = (Map<String, Long>) rect.get("origin");
    Map<String, Long> size = (Map<String, Long>) rect.get("size");

    Long x = new Long(origin.get("x"));
    Long y = new Long(origin.get("y"));

    Long height = new Long(size.get("height"));
    Long width = new Long(size.get("width"));

    UIARect res = new UIARect(x.intValue(), y.intValue(), height.intValue(), width.intValue());
    return res;
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

  @Override
  public void flickInsideWithOptions(int touchCount, UIAPoint startOffset, UIAPoint endOffset) {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.FLICK_INSIDE_WITH_OPTIONS,
        ImmutableMap.of("touchCount", touchCount, "touchCount", touchCount, "endOffset", endOffset));
    getDriver().execute(request);
  }

  public static RemoteUIAElement getFrontMostApp(RemoteUIADriver driver) {
   return  new RemoteUIAElement(driver, "1");
    
  }

}
