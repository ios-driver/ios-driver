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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.ErrorHandler;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.internal.WebElementToJsonConverter;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.UIAModels.configuration.DriverConfiguration;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.client.uiamodels.impl.configuration.RemoteDriverConfiguration;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.NoSuchElementException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class RemoteUIADriver extends RemoteWebDriver implements UIADriver, TakesScreenshot {

  private final String remoteURL;
  private final Map<String, Object> requestedCapabilities;
  // private Session session;
  private String host;
  private int port;
  private final DriverConfiguration configuration;
  private ErrorHandler errorHandler = new ErrorHandler();

  /**
   * create the remote driver, starting the remote session with the requested
   * capabilities.
   * 
   * @see #RemoteUIADriver(String, Map)
   * @param remoteURL
   * @param caps
   */
  /*
   * public RemoteUIADriver(String remoteURL, IOSCapabilities
   * requestedCapabilities) { this(remoteURL,
   * requestedCapabilities.getRawCapabilities()); }
   */

  /**
   * create the remote driver, starting the remote session with the requested
   * capabilities.
   * 
   * @param remoteURL
   * @param requestedCapabilities
   */
  /*
   * public RemoteUIADriver(String remoteURL, Map<String, Object>
   * requestedCapabilities) { this.remoteURL = remoteURL;
   * this.requestedCapabilities = requestedCapabilities;
   * 
   * try { URL url = new URL(remoteURL); port = url.getPort(); host =
   * url.getHost(); start();
   * 
   * setSessionId(getSessionId()); setCommandExecutor(new
   * HttpCommandExecutor(url)); configuration = new
   * RemoteDriverConfiguration(this);
   * 
   * } catch (MalformedURLException e) { throw new
   * IOSAutomationException("invalid URL " + remoteURL, e); } catch
   * (IOSAutomationException e) { throw e; } catch (Exception e) { throw new
   * IOSAutomationException(e); } }
   */

  /*
   * @Override protected void startClient() { }
   * 
   * @Override protected void startSession(Capabilities desiredCapabilities,
   * Capabilities requiredCapabilities) { try { session = start();
   * setSessionId(session.getSessionId());
   * 
   * 
   * } catch (Exception e) { e.printStackTrace(); } }
   */

  public RemoteUIADriver(URL url, IOSCapabilities cap) {
    super(url, cap);
    this.remoteURL = url.toExternalForm();
    this.requestedCapabilities = cap.getRawCapabilities();
    try {
      port = url.getPort();
      host = url.getHost();
      configuration = new RemoteDriverConfiguration(this);
    } catch (IOSAutomationException e) {
      throw e;
    } catch (Exception e) {
      throw new IOSAutomationException(e);
    }
  }

  /**
   * create a RemoteDriver attaching itself to an already created session.
   * Useful for debugging, not for normal tests.
   * 
   * @param remoteURL
   * @param session
   */
  public RemoteUIADriver(URL remoteURL, Session session) {
    // TODO freynaud
    this.remoteURL = remoteURL.toExternalForm();
    URL url = remoteURL;
    port = url.getPort();
    host = url.getHost();
    // TODO freynaud how safe is that ?
    this.requestedCapabilities = null;
    configuration = new RemoteDriverConfiguration(this);

  }

  /**
   * starts the remote session, and get the sessionId back.
   * 
   * @return
   * @throws Exception
   */
  /*
   * private SessionId start() throws Exception { JSONObject payload = new
   * JSONObject(); payload.put("desiredCapabilities", requestedCapabilities);
   * WebDriverLikeRequest request = new WebDriverLikeRequest("POST", "/session",
   * payload); WebDriverLikeResponse response = execute(request); String
   * sessionId = response.getSessionId(); SessionId session = new
   * SessionId(sessionId); return session; }
   */

  private IOSCapabilities loadCapabilities() {
    WebDriverLikeCommand command = WebDriverLikeCommand.GET_SESSION;
    Path p = new Path(command).withSession(getSessionId());
    WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, new JSONObject());
    Response response = execute(request);
    response = errorHandler.throwIfResponseFailed(response, -1);

    if (response.getValue() == JSONObject.NULL) {
      return null;
    } else {
      Object v = response.getValue();
      if (v instanceof String) {
        try {
          JSONObject o = new JSONObject((String) v);
          IOSCapabilities res = new IOSCapabilities(o);
          return res;
        } catch (JSONException e) {
          throw new IOSAutomationException(e);
        }
      } else {
        throw new IOSAutomationException("can't guess type, got " + v.getClass());
      }
    }
  }

  /**
   * send the request to the remote server for execution.
   * 
   * @param request
   * @return
   * @throws Exception
   */
  // TODO freynaud extract all the json parsing somwhere else.
  public Response execute(WebDriverLikeRequest request) throws IOSAutomationException {
    Response response = null;
    long total = 0;
    try {
      HttpClient client = HttpClientFactory.getClient();
      String url = remoteURL + request.getPath();
      BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest(request.getMethod(), url);
      if (request.hasPayload()) {
        r.setEntity(new StringEntity(request.getPayload().toString(), "UTF-8"));
      }

      HttpHost h = new HttpHost(host, port);
      long start = System.currentTimeMillis();
      HttpResponse res = client.execute(h, r);
      total = System.currentTimeMillis() - start;

      response = Helper.exctractResponse(res);
    } catch (Exception e) {
      throw new WebDriverException(e);
    }

    return errorHandler.throwIfResponseFailed(response, total);
  }

  @Override
  public RemoteUIATarget getLocalTarget() {
    /*
     * WebDriverLikeCommand command = WebDriverLikeCommand.LOCAL_TARGET; String
     * genericPath = command.path(); String path =
     * genericPath.replace(":sessionId", session.getSessionId());
     * WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(),
     * path); try { WebDriverLikeResponse response = execute(request); return
     * new RemoteUIATarget(this, ((JSONObject)
     * response.getValue()).getString("ref")); } catch (Exception e) { throw new
     * IOSAutomationException("Cannot get the localTarget() for the AUT.", e); }
     */
    return new RemoteUIATarget(this, "2");
  }

  @Override
  public JSONObject logElementTree(File screenshot, boolean translation) throws IOSAutomationException {
    WebDriverLikeCommand command = WebDriverLikeCommand.TREE_ROOT;
    Path p = new Path(command).withSession(getSessionId());
    return RemoteUIAElement.logElementTree(screenshot, translation, p, command, this);
  }

  /*
   * @Override public void quit() { Path p = new
   * Path(WebDriverLikeCommand.DELETE_SESSION).withSession(getSessionId());
   * WebDriverLikeRequest request = new WebDriverLikeRequest("DELETE", p);
   * execute(request);
   * 
   * }
   */

  @Override
  public IOSCapabilities getCapabilities() {
    Capabilities cap = super.getCapabilities();
    IOSCapabilities ioscap = new IOSCapabilities(cap.asMap());
    return ioscap;
  }

  /*
   * @Override public void setTimeout(String type, int timeoutInSeconds) {
   * JSONObject to = new JSONObject(); try { to.put("timeout",
   * timeoutInSeconds); to.put("type", type); WebDriverLikeCommand command =
   * WebDriverLikeCommand.SET_TIMEOUT; Path p = new
   * Path(command).withSession(getSessionId()); WebDriverLikeRequest request =
   * new WebDriverLikeRequest(command.method(), p, to); execute(request); }
   * catch (JSONException e) { e.printStackTrace(); } }
   */

  /*
   * @Override public int getTimeout(String type) {
   * 
   * try { JSONObject payload = new JSONObject(); payload.put("type", type);
   * WebDriverLikeCommand command = WebDriverLikeCommand.GET_TIMEOUT; Path p =
   * new Path(command).withSession(getSessionId()); WebDriverLikeRequest request
   * = new WebDriverLikeRequest(command.method(), p, payload);
   * WebDriverLikeResponse response = execute(request); return (Integer)
   * response.getValue(); } catch (JSONException e) { throw new
   * IOSAutomationException("JSON error", e); }
   * 
   * }
   */

  @Override
  protected WebElement findElement(String by, String using) {
    if (using == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null.");
    }

    try {
      JSONObject payload = new JSONObject();
      payload.put("using", by);
      payload.put("value", using);
      UIAElement el = (UIAElement) getRemoteObject(WebDriverLikeCommand.ELEMENT_ROOT, payload);
      setFoundBy(this, el, by, using);
      return el;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  protected List<WebElement> findElements(String by, String using) {
    if (using == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null.");
    }
    try {
      JSONObject payload = new JSONObject();
      payload.put("using", by);
      payload.put("value", using);
      List<UIAElement> all = (List<UIAElement>) getRemoteUIAElement(WebDriverLikeCommand.ELEMENTS_ROOT, payload);
      List<WebElement> allElements = new ArrayList<WebElement>();
      for (WebElement element : all) {
        allElements.add(element);
        setFoundBy(this, element, by, using);
      }
      return allElements;
    } catch (Exception e) {
      return null;
    }

  }

  @Override
  public <T extends UIAElement> T findElement(Criteria c) throws NoSuchElementException {
    try {
      JSONObject payload = new JSONObject();
      payload.put("depth", -1);
      payload.put("criteria", c.getJSONRepresentation());
      return (T) getRemoteObject(WebDriverLikeCommand.ELEMENT_ROOT, payload);
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }
  }

  @Override
  public List<UIAElement> findElements(Criteria c) {
    try {
      JSONObject payload = new JSONObject();
      payload.put("depth", -1);
      payload.put("criteria", c.getJSONRepresentation());
      return (List<UIAElement>) getRemoteObject(WebDriverLikeCommand.ELEMENTS_ROOT, payload);
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }
  }

  public RemoteIOSObject getRemoteObject(WebDriverLikeCommand command, JSONObject payload) {
    Path p = new Path(command).withSession(getSessionId());
    WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, payload);
    Response response = execute(request);
    Map<String, Object> uiaObject = ((Map<String, Object>) response.getValue());
    try {
      return RemoteIOSObject.createObject(this, uiaObject, command.returnType());
    } catch (Exception e) {
      throw new WebDriverException(e.getMessage(), e);
    }
  }

  public List<UIAElement> getRemoteUIAElement(WebDriverLikeCommand command, JSONObject payload) {
    Path p = new Path(command).withSession(getSessionId());
    WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, payload);
    Response response = execute(request);
    List<Map<String, Object>> objects = (List<Map<String, Object>>) response.getValue();
    List<UIAElement> res = new ArrayList<UIAElement>();
    for (Map<String, Object> ro : objects) {
      try {
        res.add((UIAElement) RemoteIOSObject.createObject(this, ro, command.returnType()));
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return res;
  }

  private JSONObject getJSONResult(WebDriverLikeCommand command) {

    String genericPath = command.path();
    String path = genericPath.replace(":sessionId", getSessionId().toString());
    WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), path, new JSONObject());
    Response response = execute(request);
    return ((JSONObject) response.getValue());

  }

  public static void createFileFrom64EncodedString(File f, String encoded64) throws IOException {
    byte[] img64 = Base64.decodeBase64(encoded64);
    FileOutputStream os = new FileOutputStream(f);
    os.write(img64);
    os.close();
  }

  @Override
  public Set<String> getWindowHandles() {

    try {
      JSONObject payload = new JSONObject();
      WebDriverLikeCommand command = WebDriverLikeCommand.WINDOW_HANDLES;
      Path p = new Path(command).withSession(getSessionId());

      WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, payload);
      Response response = execute(request);
      JSONArray array = ((JSONArray) response.getValue());

      Set<String> handles = new HashSet<String>();
      for (int i = 0; i < array.length(); i++) {
        handles.add(array.getString(i));
      }
      return handles;
    } catch (IOSAutomationException e) {
      throw e;
    } catch (Exception e) {
      throw new IOSAutomationException("bug", e);
    }

  }

  @Override
  public Object executeScript(String script, Object... args) {
    // Escape the quote marks
    script = script.replaceAll("\"", "\\\"");

    Iterable<Object> convertedArgs = Iterables.transform(Lists.newArrayList(args), new WebElementToJsonConverter());

    Map<String, ?> params = ImmutableMap.of("script", script, "args", Lists.newArrayList(convertedArgs));

    return execute(DriverCommand.EXECUTE_SCRIPT, params).getValue();
  }

  @Override
  public CommandConfiguration configure(WebDriverLikeCommand command) {
    return configuration.configure(command);

  }

  @Override
  public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
    // Get the screenshot as base64.
    String base64 = execute(DriverCommand.SCREENSHOT).getValue().toString();
    // ... and convert it.
    return target.convertFromBase64Png(base64);
  }

}
