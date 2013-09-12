/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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
import org.json.JSONObject;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rotatable;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.html5.LocationContext;
import org.openqa.selenium.interactions.HasTouchScreen;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteTouchScreen;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.internal.WebElementToJsonConverter;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.UIAModels.configuration.DriverConfiguration;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.Configurable;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.ElementTree;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSSearchContext;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSTouchScreen;
import org.uiautomation.ios.client.uiamodels.impl.configuration.RemoteDriverConfiguration;
import org.uiautomation.ios.client.uiamodels.impl.configuration.WebDriverLikeCommandExecutor;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


// TakesScreenshot, Rotatable, BrowserConnection, HasTouchScreen, WebStorage, LocationContext, ApplicationCache
public class RemoteIOSDriver extends RemoteWebDriver
    implements TakesScreenshot, Rotatable, LocationContext, ElementTree,
    IOSSearchContext, Configurable, HasTouchScreen, IOSTouchScreen {

  private String remoteURL;
  private Map<String, Object> requestedCapabilities;
  private String host;
  private int port;
  private DriverConfiguration configuration;
  protected WebDriverLikeCommandExecutor executor;
  private TouchScreen touchScreen;



  protected RemoteIOSDriver() {
    super();
    executor = new WebDriverLikeCommandExecutor(this);
    touchScreen = new RemoteTouchScreen(getExecuteMethod());

  }

  public RemoteIOSDriver(URL url, IOSCapabilities cap) {
    super(url, cap);
    this.remoteURL = url.toExternalForm();
    executor = new WebDriverLikeCommandExecutor(this);
    touchScreen = new RemoteTouchScreen(getExecuteMethod());
    if (cap == null) {
      this.requestedCapabilities = null;
    } else {
      this.requestedCapabilities = cap.getRawCapabilities();
    }

    port = url.getPort();
    host = url.getHost();
    configuration = new RemoteDriverConfiguration(this);


  }


  @Override
  public JSONObject logElementTree(File screenshot, boolean translation) throws WebDriverException {
    return RemoteIOSDriver.logElementTree(executor, screenshot, translation);
  }

  @Override
  public void dragFromToForDuration(Point from, Point to, int duration) throws WebDriverException {
    RemoteIOSDriver.dragFromToForDuration(executor, from, to, duration);
  }

  @Override
  public void pinchOpenFromToForDuration(Point from, Point to, int durationInSecs) {
    RemoteIOSDriver.pinchOpenFromToForDuration(executor, from, to, durationInSecs);
  }

  @Override
  public void pinchCloseFromToForDuration(Point from, Point to, int durationInSecs) {
    RemoteIOSDriver.pinchCloseFromToForDuration(executor, from, to, durationInSecs);
  }

  @Override
  public IOSCapabilities getCapabilities() {
    Capabilities cap = super.getCapabilities();
    if (cap == null) {
      return null;
    }
    IOSCapabilities ioscap = new IOSCapabilities(cap.asMap());
    return ioscap;
  }

  @Override
  protected WebElement findElement(String by, String using) {
    if (using == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null.");
    }

    WebDriverLikeRequest request = executor.buildRequest(WebDriverLikeCommand.ELEMENT_ROOT,
        ImmutableMap
            .of("using", by, "value", using));
    return executor.execute(request);

  }

  @Override
  protected List<WebElement> findElements(String by, String using) {
    if (using == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null.");
    }

    WebDriverLikeRequest request = executor.buildRequest(WebDriverLikeCommand.ELEMENTS_ROOT,
        ImmutableMap
            .of("using", by, "value", using));
    return executor.execute(request);
  }

  @Override
  public <T extends UIAElement> T findElement(Criteria c) throws NoSuchElementException {
    return RemoteIOSDriver.findElement(executor, c);
  }


  @Override
  public List<UIAElement> findElements(Criteria c) {
    return findElements(executor, c);
  }

  @Override
  public Set<String> getWindowHandles() {
    WebDriverLikeRequest request = executor.buildRequest(WebDriverLikeCommand.WINDOW_HANDLES);
    List<String> all = executor.execute(request);
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
    WebDriverLikeRequest
        request =
        executor.buildRequest(WebDriverLikeCommand.EXECUTE_SCRIPT, params);

    return executor.execute(request);
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
    return (RemoteUIAKeyboard)findElement(new TypeCriteria(UIAKeyboard.class));
  }



  @Override
  public void rotate(ScreenOrientation orientation) {
    WebDriverLikeRequest
        request =
        executor.buildRequest(WebDriverLikeCommand.SET_ORIENTATION,
            ImmutableMap.of("orientation", orientation));
    executor.execute(request);
  }

  public void rotate(Orientation orientation) {
    WebDriverLikeRequest
        request =
        executor.buildRequest(WebDriverLikeCommand.SET_ORIENTATION,
            ImmutableMap.of("orientation", orientation));
    executor.execute(request);
  }

  @Override
  public ScreenOrientation getOrientation() {
    WebDriverLikeRequest
        request =
        executor.buildRequest(WebDriverLikeCommand.GET_ORIENTATION);
    String res = executor.execute(request);
    return ScreenOrientation.valueOf(res);
  }

  public Orientation getNativeOrientation() {
    WebDriverLikeRequest
        request =
        executor.buildRequest(WebDriverLikeCommand.GET_ORIENTATION);
    String res = executor.execute(request);
    return Orientation.valueOf(res);
  }

  public Dimension getScreenSize(){
    WebDriverLikeRequest
        request =
        executor.buildRequest(WebDriverLikeCommand.GET_SCREENRECT);
    Map<String, Object> size = executor.execute(request);

    Long height = (Long) size.get("height");
    Long width = (Long) size.get("width");

    return new Dimension(width.intValue(), height.intValue());
  }

  @Override
  public void setConfiguration(WebDriverLikeCommand command, String key, Object value) {
    RemoteIOSDriver.setConfiguration(executor, command, key, value);
  }

  @Override
  public Map<String, Object> getConfiguration(WebDriverLikeCommand command) {
    return RemoteIOSDriver.getConfiguration(executor, command);
  }


  @Override
  public Location location() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void setLocation(Location location) {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public static JSONObject logElementTree(WebDriverLikeCommandExecutor executor, File screenshot,
                                          boolean translation) {
    WebDriverLikeRequest request = executor.buildRequest(WebDriverLikeCommand.TREE_ROOT,
        ImmutableMap
            .of("attachScreenshot",
                screenshot != null,
                "translation", translation));
    JSONObject log = executor.execute(request);
    if (screenshot != null) {
      JSONObject screen = log.optJSONObject("screenshot");
      String content = screen.optString("64encoded");
      RemoteUIAElement.createFileFrom64EncodedString(screenshot, content);
    }
    log.remove("screenshot");
    return log;
  }

  public static List<UIAElement> findElements(WebDriverLikeCommandExecutor executor, Criteria c) {
    WebDriverLikeRequest request = executor.buildRequest(WebDriverLikeCommand.ELEMENTS_ROOT,
        ImmutableMap
            .of("depth", -1, "criteria",
                c.stringify()));
    return executor.execute(request);
  }

  public static <T extends UIAElement> T findElement(WebDriverLikeCommandExecutor executor,
                                                     Criteria c) {
    WebDriverLikeRequest request = executor.buildRequest(WebDriverLikeCommand.ELEMENT_ROOT,
        ImmutableMap
            .of("depth", -1, "criteria",
                c.stringify()));
    return executor.execute(request);
  }

  public static Map<String, Object> getConfiguration(WebDriverLikeCommandExecutor executor,
                                                     WebDriverLikeCommand command) {

    WebDriverLikeRequest request = executor.buildRequest(
        WebDriverLikeCommand.GET_CONFIGURATION,
        null,
        new HashMap<String, Object>(),
        ImmutableMap.of("command", command.toString()));

    return executor.execute(request);
  }

  public static void setConfiguration(WebDriverLikeCommandExecutor executor,
                                      WebDriverLikeCommand command, String key, Object value) {

    WebDriverLikeRequest
        request =
        executor
            .buildRequest(WebDriverLikeCommand.CONFIGURE, null, ImmutableMap.of(key, value),
                ImmutableMap.of("command", command.toString()));
    executor.execute(request);
  }

  public TouchScreen getTouch() {
    return touchScreen;
  }


  public static void dragFromToForDuration(WebDriverLikeCommandExecutor executor, Point from,
                                      Point to, int durationInSecs){

    WebDriverLikeRequest request = executor.buildRequest(WebDriverLikeCommand.DRAG_FROM_TO_FOR_DURATION,
            ImmutableMap.of("fromX", Integer.toString(from.getX()),
                    "fromY", Integer.toString(from.getY()),
                    "toX", Integer.toString(to.getX()),
                    "toY", Integer.toString(to.getY()),
                    "duration", durationInSecs));
    executor.execute(request);
  }

  public static void pinchCloseFromToForDuration(WebDriverLikeCommandExecutor executor, Point from,
                                           Point to, int durationInSecs){

    WebDriverLikeRequest request = executor.buildRequest(WebDriverLikeCommand.PINCH_CLOSE_FROM_TO_FOR_DURATION,
            ImmutableMap.of("fromX", Integer.toString(from.getX()),
                    "fromY", Integer.toString(from.getY()),
                    "toX", Integer.toString(to.getX()),
                    "toY", Integer.toString(to.getY()),
                    "duration", durationInSecs));
    executor.execute(request);
  }

  public static void pinchOpenFromToForDuration(WebDriverLikeCommandExecutor executor, Point from,
                                                 Point to, int durationInSecs){

    WebDriverLikeRequest request = executor.buildRequest(WebDriverLikeCommand.PINCH_OPEN_FROM_TO_FOR_DURATION,
            ImmutableMap.of("fromX", Integer.toString(from.getX()),
                    "fromY", Integer.toString(from.getY()),
                    "toX", Integer.toString(to.getX()),
                    "toY", Integer.toString(to.getY()),
                    "duration", durationInSecs));
    executor.execute(request);
  }
}
