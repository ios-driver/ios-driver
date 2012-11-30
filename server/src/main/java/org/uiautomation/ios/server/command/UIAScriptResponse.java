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

import org.openqa.selenium.remote.JsonToBeanConverter;
import org.openqa.selenium.remote.Response;

public class UIAScriptResponse {
  private final String rawResponse;
  private final JsonToBeanConverter convertor = new JsonToBeanConverter();

  public UIAScriptResponse(String rawResponse) {
    this.rawResponse = rawResponse;
  }

  public String toString() {
    return rawResponse;
  }

  public boolean isFirstResponse(){
    return false;
  }
  public Response getResponse() {
    return convertor.convert(Response.class, rawResponse);
  }

  public String getRaw() {
    return rawResponse;
  }

}
