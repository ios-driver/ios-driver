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
package org.uiautomation.ios.server.command;

import java.util.Map;

import org.openqa.selenium.remote.JsonToBeanConverter;
import org.openqa.selenium.remote.Response;

public class UIAScriptResponse {
  private final String rawResponse;
  private final JsonToBeanConverter convertor = new JsonToBeanConverter();
  private boolean firstResponse = false;
  private final Response response;

  public UIAScriptResponse(String rawResponse) {
    this.rawResponse = rawResponse;
    response = convertor.convert(Response.class, rawResponse);
    if (isFirstResponse(response)) {
      firstResponse = true;
      Object realResponse = ((Map<String, Object>) response.getValue()).get("firstResponse");
      response.setValue(realResponse);
    }
  }

  public String toString() {
    return rawResponse;
  }

  public boolean isFirstResponse() {
    return firstResponse;
  }

  private boolean isFirstResponse(Response r) {
    if (r.getValue() instanceof Map) {
      return ((Map<String, Object>) r.getValue()).containsKey("firstResponse");
    }
    return false;
  }

  public Response getResponse() {
    return response;
  }

  public String getRaw() {
    return rawResponse;
  }

}
