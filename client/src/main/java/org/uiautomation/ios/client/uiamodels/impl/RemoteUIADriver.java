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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Keyboard;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rotatable;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.html5.LocationContext;
import org.openqa.selenium.remote.BeanToJsonConverter;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.ErrorHandler;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.internal.WebElementToJsonConverter;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.UIAAlert;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


// TakesScreenshot, Rotatable, BrowserConnection, HasTouchScreen, WebStorage, LocationContext, ApplicationCache
public class RemoteUIADriver extends RemoteWebDriver
    implements UIADriver, TakesScreenshot, Rotatable, LocationContext {

  private String remoteURL;
  private Map<String, Object> requestedCapabilities;
  // private Session session;
  private String host;
  private int port;
  private DriverConfiguration configuration;
  private ErrorHandler errorHandler = new ErrorHandler();


  protected RemoteUIADriver() {
    super();
  }
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
   * create the remote driver, starting the remote session with the requested capabilities.
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
    if (cap == null) {
      this.requestedCapabilities = null;
    } else {
      this.requestedCapabilities = cap.getRawCapabilities();
    }

    port = url.getPort();
    host = url.getHost();
    configuration = new RemoteDriverConfiguration(this);

  }

  public WebDriverLikeRequest buildRequest(WebDriverLikeCommand command, RemoteUIAElement element,
                                           Map<String, ?> params,
                                           Map<String, String> extraParamInPath) {
    String method = command.method();
    Path p = new Path(command).withSession(getSessionId());
    if (element != null) {
      p.withReference(element.getReference());
    }
    for (String key : extraParamInPath.keySet()) {
      p.validateAndReplace(":" + key, extraParamInPath.get(key));
    }
    WebDriverLikeRequest request = new WebDriverLikeRequest(method, p, params);
    return request;
  }

  public WebDriverLikeRequest buildRequest(WebDriverLikeCommand command, RemoteUIAElement element,
                                           Map<String, ?> params) {
    return buildRequest(command, element, params, new HashMap<String, String>());
  }

  public WebDriverLikeRequest buildRequest(WebDriverLikeCommand command) {
    return buildRequest(command, null, null);
  }

  public WebDriverLikeRequest buildRequest(WebDriverLikeCommand command, Map<String, ?> params) {
    return buildRequest(command, null, params);
  }

  /**
   * send the request to the remote server for execution.
   */
  public <T> T execute(WebDriverLikeRequest request) {
    Response response = null;
    long total = 0;
    try {
      HttpClient client = HttpClientFactory.getClient();
      String url = remoteURL + request.getPath();
      BasicHttpEntityEnclosingRequest
          r =
          new BasicHttpEntityEnclosingRequest(request.getMethod(), url);
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
    response = errorHandler.throwIfResponseFailed(response, total);
    return cast(response.getValue());
  }

  @Override
  public JSONObject logElementTree(File screenshot, boolean translation) throws WebDriverException {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.TREE_ROOT,
                                                ImmutableMap
                                                    .of("attachScreenshot", screenshot != null,
                                                        "translation", translation));
    JSONObject log = execute(request);
    if (screenshot != null) {
      JSONObject screen = log.optJSONObject("screenshot");
      String content = screen.optString("64encoded");
      RemoteUIAElement.createFileFrom64EncodedString(screenshot, content);
    }
    log.remove("screenshot");
    return log;
  }

  @Override
  public IOSCapabilities getCapabilities() {
    Capabilities cap = super.getCapabilities();
    IOSCapabilities ioscap = new IOSCapabilities(cap.asMap());
    return ioscap;
  }

  @Override
  protected WebElement findElement(String by, String using) {
    if (using == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null.");
    }

    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENT_ROOT,
                                                ImmutableMap.of("using", by, "value", using));
    return execute(request);

  }

  @Override
  protected List<WebElement> findElements(String by, String using) {
    if (using == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null.");
    }

    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENTS_ROOT,
                                                ImmutableMap.of("using", by, "value", using));
    return execute(request);
  }

  @Override
  public <T extends UIAElement> T findElement(Criteria c) throws NoSuchElementException {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENT_ROOT,
                                                ImmutableMap
                                                    .of("depth", -1, "criteria", c.stringify()));
    return execute(request);
  }

  @Override
  public List<UIAElement> findElements(Criteria c) {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENTS_ROOT,
                                                ImmutableMap
                                                    .of("depth", -1, "criteria", c.stringify()));
    return execute(request);
  }

  /*
   * public RemoteIOSObject getRemoteObject(WebDriverLikeCommand command,
   * JSONObject payload) { Path p = new
   * Path(command).withSession(getSessionId()); WebDriverLikeRequest request =
   * new WebDriverLikeRequest(command.method(), p, payload); Response response =
   * execute(request); Map<String, Object> uiaObject = ((Map<String, Object>)
   * response.getValue()); try { return RemoteIOSObject.createObject(this,
   * uiaObject, command.returnType()); } catch (Exception e) { throw new
   * WebDriverException(e.getMessage(), e); } }
   */

  /*
   * public List<UIAElement> getRemoteUIAElements(WebDriverLikeCommand command,
   * JSONObject payload) { Path p = new
   * Path(command).withSession(getSessionId()); WebDriverLikeRequest request =
   * new WebDriverLikeRequest(command.method(), p, payload); Response response =
   * execute(request); List<Map<String, Object>> objects = (List<Map<String,
   * Object>>) response.getValue(); List<UIAElement> res = new
   * ArrayList<UIAElement>(); for (Map<String, Object> ro : objects) { try {
   * res.add((UIAElement) RemoteIOSObject.createObject(this, ro,
   * command.returnType())); } catch (Exception e) { // TODO Auto-generated
   * catch block e.printStackTrace(); } } return res; }
   */

  private <T> T cast(Object o) {
    if (o == null) {
      return null;
    }
    if (o instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) o;
      if (map.containsKey("ELEMENT")) {
        return (T) RemoteIOSObject.createObject(this, map);
      } else if (map.containsKey("tree")) {
        String serialized = new BeanToJsonConverter().convert(o);
        try {
          return (T) new JSONObject(serialized);
        } catch (JSONException e) {
          throw new WebDriverException("cannot cast");
        }
      } else {
        return (T) map;
      }
    }
    if (o instanceof Collection) {
      List<Object> res = new ArrayList<Object>();
      Collection c = (Collection<Object>) o;
      for (Object ob : c) {
        res.add(cast(ob));
      }
      return (T) res;
    }
    return (T) o;
  }

  /*
   * private JSONObject getJSONResult(WebDriverLikeCommand command) {
   * 
   * String genericPath = command.path(); String path =
   * genericPath.replace(":sessionId", getSessionId().toString());
   * WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(),
   * path, new JSONObject()); Response response = execute(request); return
   * ((JSONObject) response.getValue());
   * 
   * }
   */

  public static void createFileFrom64EncodedString(File f, String encoded64) throws IOException {
    byte[] img64 = Base64.decodeBase64(encoded64);
    FileOutputStream os = new FileOutputStream(f);
    os.write(img64);
    os.close();
  }

  @Override
  public Set<String> getWindowHandles() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.WINDOW_HANDLES);
    List<String> all = execute(request);
    Set<String> res = new HashSet<String>();
    res.addAll(all);
    return res;
  }

  @Override
  public Object executeScript(String script, Object... args) {
    // Escape the quote marks
    script = script.replaceAll("\"", "\\\"");

    Iterable<Object>
        convertedArgs =
        Iterables.transform(Lists.newArrayList(args), new WebElementToJsonConverter());
    Map<String, ?>
        params =
        ImmutableMap.of("script", script, "args", Lists.newArrayList(convertedArgs));
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.EXECUTE_SCRIPT, params);

    return execute(request);
    //return execute(DriverCommand.EXECUTE_SCRIPT, params).getValue();
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

  @Override
  public Keyboard getKeyboard() {
    WebDriverLikeRequest
        request =
        buildRequest(WebDriverLikeCommand.KEYBOARD, RemoteUIAElement.getFrontMostApp(this),
                     null);
    return execute(request);
  }

  @Override
  public void tap(int x, int y) {
    WebDriverLikeRequest
        request =
        buildRequest(WebDriverLikeCommand.TARGET_TAP, RemoteUIAElement.target(this),
                     ImmutableMap.of("x", x, "y", y));
    execute(request);
  }


  @Override
  public RemoteUIAAlert getAlert() throws NoAlertPresentException {
    RemoteUIAAlert alert = new RemoteUIAAlert(this, "3");
    // but need to access it once with a call just to check if an alert actually
    // exists. If not,
    // throw a no alert exception
    alert.getName();
    return alert;
  }

  @Override
  public void pinchClose(int x1, int y1, int x2, int y2, int duration) {
    /*WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.PINCH_CLOSE,
                                                ImmutableMap
                                                    .of("x1", x1, "y1", y1, "x2", x2, "y2", y2,
                                                        "duration", duration));
    execute(request); */
  }

  @Override
  public void rotate(ScreenOrientation orientation) {
    WebDriverLikeRequest
        request =
        buildRequest(WebDriverLikeCommand.SET_ORIENTATION,
                     ImmutableMap.of("orientation", orientation));
    execute(request);
  }

  public void rotate(Orientation orientation) {
    WebDriverLikeRequest
        request =
        buildRequest(WebDriverLikeCommand.SET_ORIENTATION,
                     ImmutableMap.of("orientation", orientation));
    execute(request);
  }

  @Override
  public ScreenOrientation getOrientation() {
    WebDriverLikeRequest
        request =
        buildRequest(WebDriverLikeCommand.GET_ORIENTATION);
    String res = execute(request);
    return ScreenOrientation.valueOf(res);
  }

  public Orientation getNativeOrientation() {
    WebDriverLikeRequest
        request =
        buildRequest(WebDriverLikeCommand.GET_ORIENTATION);
    String res = execute(request);
    return Orientation.valueOf(res);
  }

  @Override
  public Location location() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void setLocation(Location location) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
