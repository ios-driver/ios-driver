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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.internal.WebElementToJsonConverter;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.UIAModels.configuration.DriverConfiguration;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.client.uiamodels.impl.configuration.RemoteDriverConfiguration;
import org.uiautomation.ios.communication.FailedWebDriverLikeResponse;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.NoSuchElementException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class RemoteUIADriver extends RemoteWebDriver implements UIADriver {

  private final String remoteURL;
  private final Map<String, Object> requestedCapabilities;
  private final Session session;
  private String host;
  private int port;
  private final DriverConfiguration configuration;

  /**
   * create the remote driver, starting the remote session with the requested
   * capabilities.
   * 
   * @see #RemoteUIADriver(String, Map)
   * @param remoteURL
   * @param caps
   */
  public RemoteUIADriver(String remoteURL, IOSCapabilities requestedCapabilities) {
    this(remoteURL, requestedCapabilities.getRawCapabilities());
  }

  /**
   * create the remote driver, starting the remote session with the requested
   * capabilities.
   * 
   * @param remoteURL
   * @param requestedCapabilities
   */
  public RemoteUIADriver(String remoteURL, Map<String, Object> requestedCapabilities) {
    this.remoteURL = remoteURL;
    this.requestedCapabilities = requestedCapabilities;

    try {
      URL url = new URL(remoteURL);
      port = url.getPort();
      host = url.getHost();
      session = start();

      setSessionId(session.getSessionId());
      setCommandExecutor(new HttpCommandExecutor(url));
      configuration = new RemoteDriverConfiguration(this);

    } catch (MalformedURLException e) {
      throw new IOSAutomationException("invalid URL " + remoteURL, e);
    } catch (IOSAutomationException e) {
      throw e;
    } catch (Exception e) {
      throw new IOSAutomationException(e);
    }
  }

  @Override
  protected void startClient() {
  }

  @Override
  protected void startSession(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {

  }

  public RemoteUIADriver(URL url, IOSCapabilities cap) {
    super(url, cap);
    this.remoteURL = url.toExternalForm();
    this.requestedCapabilities = cap.getRawCapabilities();
    try {

      port = url.getPort();
      host = url.getHost();
      session = start();

      setSessionId(session.getSessionId());
      setCommandExecutor(new HttpCommandExecutor(url));
      configuration = new RemoteDriverConfiguration(this);

    } catch (MalformedURLException e) {
      throw new IOSAutomationException("invalid URL " + remoteURL, e);
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
    this.remoteURL = remoteURL.toExternalForm();
    URL url = remoteURL;
    port = url.getPort();
    host = url.getHost();
    this.session = session;
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
  private Session start() throws Exception {
    JSONObject payload = new JSONObject();
    payload.put("desiredCapabilities", requestedCapabilities);
    WebDriverLikeRequest request = new WebDriverLikeRequest("POST", "/session", payload);
    WebDriverLikeResponse response = execute(request);
    String sessionId = response.getSessionId();
    Session session = new Session(sessionId);
    return session;
  }

  /**
   * send the request to the remote server for execution.
   * 
   * @param request
   * @return
   * @throws Exception
   */
  // TODO freynaud extract all the json parsing somwhere else.
  public WebDriverLikeResponse execute(WebDriverLikeRequest request) throws IOSAutomationException {
    int returnCode = 13;
    JSONObject o = null;

    try {
      HttpClient client = HttpClientFactory.getClient();
      String url = remoteURL + request.getPath();
      BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest(request.getMethod(), url);
      if (request.hasPayload()) {
        // String normalizedOriginalText =
        // Normalizer.normalize(request.getPayload().toString(),
        // Form.NFC);
        r.setEntity(new StringEntity(request.getPayload().toString(), "UTF-8"));
      }

      HttpHost h = new HttpHost(host, port);
      long start = System.currentTimeMillis();
      HttpResponse response = client.execute(h, r);
      long total = System.currentTimeMillis() - start;
      // System.out.println(total + " ms \tfor " + request.getGenericCommand());

      o = Helper.extractObject(response);
      returnCode = o.getInt("status");
    } catch (Exception e) {
      throw new IOSAutomationException(e);
    }

    if (returnCode == 0) {
      return new WebDriverLikeResponse(o);
    } else {
      JSONObject value;
      Throwable t = null;
      try {
        value = o.getJSONObject("value");
      } catch (JSONException e) {
        throw new IOSAutomationException(e);
      }
      t = FailedWebDriverLikeResponse.createThrowable(value);
      if (t instanceof IOSAutomationException) {
        throw (IOSAutomationException) t;
      } else {
        throw new IOSAutomationException("The server threw an unexpected exception", t);
      }
    }
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
    Path p = new Path(command).withSession(session.getSessionId());
    return RemoteUIAElement.logElementTree(screenshot, translation, p, command, this);
  }

  @Override
  public Session getSession() {
    return session;
  }

  @Override
  public void quit() {
    Path p = new Path(WebDriverLikeCommand.DELETE_SESSION).withSession(session.getSessionId());
    WebDriverLikeRequest request = new WebDriverLikeRequest("DELETE", p);
    execute(request);

  }

  private IOSCapabilities cached;

  @Override
  public IOSCapabilities getCapabilities() {
    if (cached == null) {
      WebDriverLikeCommand command = WebDriverLikeCommand.GET_SESSION;
      Path p = new Path(command).withSession(session.getSessionId());
      WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, new JSONObject());
      WebDriverLikeResponse response;
      try {
        response = execute(request);
      } catch (IOSAutomationException e) {
        throw e;
      } catch (Exception e) {
        throw new IOSAutomationException(e);
      }
      if (response.getValue() == JSONObject.NULL) {
        return null;
      } else {
        Object v = response.getValue();
        if (v instanceof String) {
          try {
            JSONObject o = new JSONObject((String) v);
            IOSCapabilities res = new IOSCapabilities(o);
            // cached = res;
            return res;
          } catch (JSONException e) {
            throw new IOSAutomationException(e);
          }
        } else {
          throw new IOSAutomationException("can't guess type, got " + v.getClass());
        }
      }
    } else {
      return cached;
    }

  }

  @Override
  public void setTimeout(String type, int timeoutInSeconds) {
    JSONObject to = new JSONObject();
    try {
      to.put("timeout", timeoutInSeconds);
      to.put("type", type);
      WebDriverLikeCommand command = WebDriverLikeCommand.SET_TIMEOUT;
      Path p = new Path(command).withSession(session.getSessionId());
      WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, to);
      execute(request);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public int getTimeout(String type) {

    try {
      JSONObject payload = new JSONObject();
      payload.put("type", type);
      WebDriverLikeCommand command = WebDriverLikeCommand.GET_TIMEOUT;
      Path p = new Path(command).withSession(session.getSessionId());
      WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, payload);
      WebDriverLikeResponse response = execute(request);
      return (Integer) response.getValue();
    } catch (JSONException e) {
      throw new IOSAutomationException("JSON error", e);
    }

  }

  @Override
  public UIAElement findElement(Criteria c) throws NoSuchElementException {
    try {
      JSONObject payload = new JSONObject();
      payload.put("depth", -1);
      payload.put("criteria", c.getJSONRepresentation());
      return (UIAElement) getRemoteObject(WebDriverLikeCommand.ELEMENT_ROOT, payload);
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }
  }

  @Override
  public UIAElementArray<UIAElement> findElements(Criteria c) {
    try {
      JSONObject payload = new JSONObject();
      payload.put("depth", -1);
      payload.put("criteria", c.getJSONRepresentation());
      return (UIAElementArray<UIAElement>) getRemoteObject(WebDriverLikeCommand.ELEMENTS_ROOT, payload);
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }
  }

  public RemoteObject getRemoteObject(WebDriverLikeCommand command, JSONObject payload) {
    try {
      Path p = new Path(command).withSession(getSession().getSessionId());

      WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, payload);
      WebDriverLikeResponse response = execute(request);
      JSONObject uiaObject = ((JSONObject) response.getValue());

      return RemoteObject.createObject(this, uiaObject, command.returnType());
    } catch (IOSAutomationException e) {
      throw e;
    } catch (Exception e) {
      throw new IOSAutomationException("bug", e);
    }
  }

  @Override
  public void takeScreenshot(String path) {
    try {
      // TODO freynaud use getObject ?
      JSONObject res = getJSONResult(WebDriverLikeCommand.SCREENSHOT);
      String content = res.getString("64encoded");
      createFileFrom64EncodedString(new File(path), content);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private JSONObject getJSONResult(WebDriverLikeCommand command) {

    String genericPath = command.path();
    String path = genericPath.replace(":sessionId", session.getSessionId());
    WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), path, new JSONObject());
    WebDriverLikeResponse response;
    try {
      response = execute(request);
      return ((JSONObject) response.getValue());
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return null;
    }
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
      Path p = new Path(command).withSession(getSession().getSessionId());

      WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, payload);
      WebDriverLikeResponse response = execute(request);
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
}
