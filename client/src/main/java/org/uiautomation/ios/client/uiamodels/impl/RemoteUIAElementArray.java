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

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class RemoteUIAElementArray<T extends UIAElement> extends RemoteObject
    implements
      UIAElementArray<T> {

  private final int length;


  public RemoteUIAElementArray(RemoteUIADriver driver, String reference, Integer size) {
    super(driver, reference);
    length = size;
  }

  public static RemoteUIAElementArray<UIAElement> emptyArray(RemoteUIADriver driver) {
    return new RemoteUIAElementArray<UIAElement>(driver, null, 0);
  }

  @Override
  public int size() {
    return length;
  }


  @SuppressWarnings("unchecked")
  @Override
  public T get(int index) {
    try {
      JSONObject payload = new JSONObject();
      payload.put("index", index);
      return (T) getRemoteObject(WebDriverLikeCommand.GET, payload);
    } catch (Exception e) {
      throw new IOSAutomationException(e);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public T getFirst(Criteria c) {
    try {
      JSONObject payload = new JSONObject();
      payload.put("depth", -1);
      payload.put("criteria", c.getJSONRepresentation());
      return (T) getRemoteObject(WebDriverLikeCommand.ELEMENT, payload);
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }
   
  }

  @SuppressWarnings("unchecked")
  @Override
  public UIAElementArray<T> getAll(Criteria c) {
    try {
      JSONObject payload = new JSONObject();
      payload.put("depth", -1);
      payload.put("criteria", c.getJSONRepresentation());
      return (UIAElementArray<T>) getRemoteObject(WebDriverLikeCommand.ELEMENTS, payload);
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }
  }
  
  @Deprecated
  public T getFirstWithName(String name) {
    if (length == 0) {
      return null;
    }
    try {
      JSONObject payload = new JSONObject();
      payload.put("name", "'" + name + "'");
      return (T) getRemoteObject(WebDriverLikeCommand.FIRST_WITH_NAME, payload);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }



  @Deprecated
  public UIAElementArray<T> getWithName(String name) {
    try {
      JSONObject payload = new JSONObject();
      payload.put("name", "'" + name + "'");
      return (UIAElementArray<T>) getRemoteObject(WebDriverLikeCommand.FIRST_WITH_NAME, payload);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }



 
  public Iterator<T> iterator() {
    return new RemoteUIAElementArrayIterator<T>(this);
  }


  @Override
  public String toString() {
    return super.toString() + ",length = " + size();
  }

}
