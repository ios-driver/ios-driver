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

import java.util.ArrayList;
import java.util.List;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.instruments.CommunicationChannel;
import org.uiautomation.ios.server.instruments.IOSDriver;
import org.uiautomation.ios.server.instruments.SessionsManager;

public abstract class BaseCommandHandler implements Handler {

  private final IOSDriver driver;
  private final WebDriverLikeRequest request;
  private final List<PreHandleDecorator> preDecorators = new ArrayList<PreHandleDecorator>();
  private final List<PostHandleDecorator> postDecorators = new ArrayList<PostHandleDecorator>();

  public BaseCommandHandler(IOSDriver driver, WebDriverLikeRequest request) {
    this.driver = driver;
    this.request = request;
  }

  @Override
  public void addDecorator(PostHandleDecorator decorator) {
    postDecorators.add(decorator);

  }

  @Override
  public void addDecorator(PreHandleDecorator decorator) {
    preDecorators.add(decorator);
  }

  protected IOSDriver getDriver() {
    return driver;
  }


  protected WebDriverLikeRequest getRequest() {
    return request;
  }

  public CommunicationChannel communication() {
    ServerSideSession session = getDriver().getSession(request.getSession());
    return session.communication();
  }

  @Override
  public WebDriverLikeResponse handleAndRunDecorators() throws Exception {
    for (PreHandleDecorator pre : preDecorators) {
      pre.decorate(request);
    }
    WebDriverLikeResponse response = handle();
    for (PostHandleDecorator post : postDecorators) {
      post.decorate(response);
    }
    return response;
  }

}
