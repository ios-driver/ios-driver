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

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIATextView;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class RemoteUIATextView extends RemoteUIAElement implements UIATextView {

  public RemoteUIATextView(RemoteUIADriver driver, String reference) {
    super(driver, reference);
  }

  @Override
  public void setValue(String value) {
    JSONObject payload = new JSONObject();
    try {
      value = value.replaceAll("\\\\", "\\\\\\\\");
      value = value.replaceAll("\\n", "\\\\n");
      value = value.replaceAll("\\t", "\\\\t");
      payload.put("value", "'" + value + "'");
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }
    execute(WebDriverLikeCommand.SET_VALUE, payload);
  }

}
