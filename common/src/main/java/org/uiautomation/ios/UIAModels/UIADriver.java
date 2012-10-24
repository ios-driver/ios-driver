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
package org.uiautomation.ios.UIAModels;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.NoSuchElementException;


public interface UIADriver {

  public Session getSession();
  
  public UIATarget getLocalTarget();

  public IOSCapabilities getCapabilities() throws Exception;

  public JSONObject logElementTree(File screenshot,boolean translation) throws IOSAutomationException;

  public void quit();
  
  public void setTimeout(String type,int timeoutInSeconds);
 
  public int getTimeout(String type);

  public UIAElement findElement(Criteria c) throws NoSuchElementException;

  public <T> T findElement(Class<T> type, Criteria c) throws NoSuchElementException;


}
