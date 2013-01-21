/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios.context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.uiautomation.ios.mobileSafari.IosAtoms;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.webInspector.DOM.DOM;
import org.uiautomation.ios.webInspector.DOM.Page;
import org.uiautomation.ios.webInspector.DOM.RemoteObject;
import org.uiautomation.ios.webInspector.DOM.RemoteObjectArray;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public abstract class BaseWebInspector implements WebDriver, JavascriptExecutor {

  private static final Logger log = Logger.getLogger(BaseWebInspector.class.getName());

  protected final ServerSideSession session;

  protected BaseWebInspector(ServerSideSession session) {
    this.session = session;
  }

  public abstract JSONObject sendCommand(JSONObject command);

  public abstract int getPageIdentifier();


  public RemoteWebElement getDocument() {
    log.warning("TODO : frames");
    /*DOMContext context = session.getContext().getDOMContext();
    RemoteWebElement result = context.getDocument();
    if (result == null) {                             */
    RemoteWebElement result = retrieveDocumentAndCheckReady();
    RemoteWebElement window = getMainWindow();
    //context.setCurrentFrame(null, result, window);
    //}
    return result;
  }


  public RemoteWebElement getMainWindow() {
    return new RemoteWebElement(new NodeId(0), this);
  }


  private RemoteWebElement retrieveDocumentAndCheckReady() {
    RemoteWebElement element;
    long start = System.currentTimeMillis();
    while (true) {
      try {
        element = retrieveDocument();
        return element;
      } catch (Exception e) {
        log.warning("Workaround in DOMContext, the given document is corrupted, nodeId ");
      }
    }
//    return element;
  }


  private RemoteWebElement retrieveDocument() throws Exception {
    JSONObject result = sendCommand(DOM.getDocument());
    JSONObject root = result.getJSONObject("root");
    RemoteWebElement rme = new RemoteWebElement(new NodeId(root.getInt("nodeId")), this);
    return rme;
  }


  public void get(String url) {
    JSONObject command = Page.navigate(url);
    sendCommand(command);
  }


  public String getCurrentUrl() {

    try {
      RemoteWebElement document = getDocument();
      String f = "(function(arg) { var url=this.URL;return url;})";
      JSONObject cmd = new JSONObject();

      cmd.put("method", "Runtime.callFunctionOn");

      JSONArray args = new JSONArray();

      cmd.put("params", new JSONObject().put("objectId", document.getRemoteObject().getId())
          .put("functionDeclaration", f).put("arguments", args).put("returnByValue", true));

      JSONObject response = sendCommand(cmd);
      return cast(response);
    } catch (Exception e) {
      throw new WebDriverException(e);
    }


  }


  public String getTitle() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public List<WebElement> findElements(By by) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public WebElement findElement(By by) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public String getPageSource() {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Runtime.evaluate");
      cmd.put("params", new JSONObject().put("expression", "document.documentElement.outerHTML;")
          .put("returnByValue", true));
      JSONObject response = sendCommand(cmd);
      return cast(response);
    } catch (Exception e) {
      throw new WebDriverException(e);
    }

  }


  public void close() {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public void quit() {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public Set<String> getWindowHandles() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public String getWindowHandle() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public TargetLocator switchTo() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public Navigation navigate() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public Options manage() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public <T> T cast(JSONObject response) {
    JSONObject body = null;
    try {
      body = response.has("result") ? response.getJSONObject("result")
                                    : response.getJSONObject("object");
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }

    if (body == null) {
      throw new RuntimeException("Error parsting " + response);
    }

    try {
      return cast_(body);
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }


  }

  private <T> T cast_(JSONObject body) throws JSONException {
    List<String> primitives = new ArrayList<String>();
    primitives.add("boolean");
    primitives.add("number");
    primitives.add("string");

    String type = body.getString("type");
    // handle null return
    if ("undefined".equals(type)) {
      return (T) null;
    }

    // handle primitive types.
    if (primitives.contains(type)) { // primitive type.
      Object value = body.get("value");
      return (T) value;
    }

    // handle objects
    if ("object".equals(type)) {
      if (body.has("value") && body.isNull("value")) {
        return (T) null;
      }

      if ("array".equals(body.optString("subtype"))) {
        RemoteObject array = new RemoteObject(body.getString("objectId"), this);
        RemoteObjectArray a = new RemoteObjectArray(array);
        ArrayList<Object> res = new ArrayList<Object>();
        for (Object ro : a) {
          res.add(ro);
        }
        return (T) res;
      }

      if (body.has("objectId")) {
        if ("node".equals(body.optString("subtype")) || "Window"
            .equals(body.optString("className"))) {
          return (T) new RemoteObject(body.getString("objectId"), this);
        } else {
          RemoteObject ro = new RemoteObject(body.getString("objectId"), this);
          JSONObject o = new JSONObject(ro.stringify());
          return (T) o;
        }

      }
      return (T) new RemoteObject(body.getString("objectId"), this);

    }
    throw new RuntimeException("NI " + body);
  }


  public Object executeScript(String script, Object... args) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public Object executeAsyncScript(String script, Object... args) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public int getInnerWidth() throws JSONException {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.evaluate");
    cmd.put("params", new JSONObject().put("expression", "document.body.clientWidth;"));

    JSONObject response = sendCommand(cmd);
    return (Integer) cast(response);
  }

  public Dimension getSize() throws Exception {
    String
        f =
        "(function(element) { var result = " + IosAtoms.GET_INTERACTABLE_SIZE + "(window.top);"
        + "var res = " + IosAtoms.STRINGIFY + "(result);"
        + "return  res;  })";
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    cmd.put("params",
            new JSONObject()
                .put("objectId", getDocument().getRemoteObject().getId())
                .put("functionDeclaration", f)
                .put("returnByValue", false));

    JSONObject response = sendCommand(cmd);
    String s = cast(response);
    JSONObject o = new JSONObject(s);
    Dimension dim = new Dimension(o.getInt("width"), o.getInt("height"));
    return dim;

  }


  public RemoteWebElement findElementByCSSSelector(String cssSelector) {
    RemoteWebElement document = getDocument();
    return document.findElementByCSSSelector(cssSelector);
  }

  public List<RemoteWebElement> findElementsByCSSSelector(String cssSelector) {
    RemoteWebElement document = getDocument();
    return document.findElementsByCSSSelector(cssSelector);
  }
}
