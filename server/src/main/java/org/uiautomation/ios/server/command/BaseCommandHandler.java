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

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.instruments.CommunicationChannel;

public abstract class BaseCommandHandler implements Handler {

  private final IOSDriver driver;
  private final ServerSideSession session;
  private final WebDriverLikeRequest request;
  private final List<PreHandleDecorator> preDecorators = new ArrayList<PreHandleDecorator>();
  private final List<PostHandleDecorator> postDecorators = new ArrayList<PostHandleDecorator>();

  public BaseCommandHandler(IOSDriver driver, WebDriverLikeRequest request) {
    this.driver = driver;
    this.request = request;

    if (request.hasVariable(":sessionId")) {
      session = driver.getSession(request.getSession());
    } else {
      session = null;
    }
  }

  public ServerSideSession getSession() {
    return session;
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
  public Response handleAndRunDecorators() throws Exception {
    for (PreHandleDecorator pre : preDecorators) {
      pre.decorate(request);
    }
    Response response = handle();
    for (PostHandleDecorator post : postDecorators) {
      post.decorate(response);
    }
    return response;
  }

  protected <T> T getConf(String key) {
    return getConf(key, (T) null);
  }

  protected <T> T getConf(String key, T defaultValue) {
    CommandConfiguration conf = getSession().configure(getRequest().getGenericCommand());
    T res = (T) conf.get(key);
    return res != null ? res : defaultValue;
  }

  public abstract JSONObject configurationDescription() throws JSONException;

  protected JSONObject noConfigDefined() throws JSONException {
    JSONObject res = new JSONObject().put("No config for this command", "");
    return res;
  }
  
  
  protected Response createResponse(Object value){
    Response r = new Response();
    r.setSessionId(getSession().getSessionId());
    r.setStatus(0);
    r.setValue(value);
    return r;
  }

}
