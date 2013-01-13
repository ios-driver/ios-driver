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

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.SessionId;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

import java.net.URL;
import java.util.Map;

public class AttachRemoteUIADriver extends RemoteUIADriver {

  @Override
  protected void startClient() {
  }

  @Override
  protected void startSession(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
  }

  public AttachRemoteUIADriver(URL url, SessionId session) {
    super(url, null);
    setCommandExecutor(new HttpCommandExecutor(url));
    setSessionId(session.toString());
  }

  IOSCapabilities caps = null;

  @Override
  public IOSCapabilities getCapabilities() {
    if (caps == null) {
      caps = loadCapabilities();
    }
    return caps;
  }

  private IOSCapabilities loadCapabilities() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.GET_SESSION);
    Map<String, Object> c = execute(request);
    return new IOSCapabilities(c);
  }


}
