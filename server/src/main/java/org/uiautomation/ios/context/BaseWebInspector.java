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
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.mobileSafari.IosAtoms;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.events.ChildNodeRemoved;
import org.uiautomation.ios.mobileSafari.events.Event;
import org.uiautomation.ios.mobileSafari.events.EventFactory;
import org.uiautomation.ios.mobileSafari.events.inserted.ChildIframeInserted;
import org.uiautomation.ios.mobileSafari.message.ApplicationDataMessage;
import org.uiautomation.ios.mobileSafari.message.ApplicationSentListingMessage;
import org.uiautomation.ios.mobileSafari.message.IOSMessage;
import org.uiautomation.ios.mobileSafari.remoteWebkitProtocol.MessageListener;
import org.uiautomation.ios.server.DOMContext;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.webInspector.DOM.DOM;
import org.uiautomation.ios.webInspector.DOM.Page;
import org.uiautomation.ios.webInspector.DOM.RemoteObject;
import org.uiautomation.ios.webInspector.DOM.RemoteObjectArray;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

import sun.net.dns.ResolverConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public abstract class BaseWebInspector implements MessageListener {

  private static final Logger log = Logger.getLogger(BaseWebInspector.class.getName());
  protected final ServerSideSession session;
  private boolean newPage = true;
  public static final Long defaultPageLoadTimeoutInMs = 30000L;
  private final DOMContext context;

  protected BaseWebInspector(ServerSideSession session) {
    this.session = session;
    this.context = new DOMContext(this);
  }

  public abstract JSONObject sendCommand(JSONObject command);

  public abstract int getPageIdentifier();

  public RemoteWebElement getDocument() {
    long deadline = System.currentTimeMillis() + defaultPageLoadTimeoutInMs;
    return getDocument(deadline);
  }

  public RemoteWebElement getDocument(long deadline) {
    RemoteWebElement result = context.getDocument();
    if (result == null) {
      result = retrieveDocumentAndCheckReady(deadline);
      RemoteWebElement window = getMainWindow();
      context.setCurrentFrame(null, result, window);
    }
    return result;
  }


  public RemoteWebElement getMainWindow() {
    return new RemoteWebElement(new NodeId(0), this);
  }


  private RemoteWebElement retrieveDocumentAndCheckReady(long deadline) {
    RemoteWebElement element = null;
    String readyState = "";
    while (!readyState.equals("complete")) {
      if (deadline > 0 && System.currentTimeMillis() > deadline) {
        throw new TimeoutException("Timeout waiting to get the document.");
      }
      try {
        log.fine("trying to get the document");
        element = retrieveDocument();
        log.fine("got it");
        readyState = element.getRemoteObject().call(".readyState");
        log.fine("ready ? " + readyState);
      } catch (Exception e) {
        log.warning(
            "The given document is corrupted, nodeId=" + element.getNodeId() + e.getMessage());
      }
    }
    return element;
  }


  private RemoteWebElement retrieveDocument() throws Exception {
    JSONObject result = sendCommand(DOM.getDocument());
    JSONObject root = result.getJSONObject("root");
    RemoteWebElement rme = new RemoteWebElement(new NodeId(root.getInt("nodeId")), this);
    return rme;
  }


  public void get(String url) {

    try {
      context.eventsLock().lock();
      JSONObject command = Page.navigate(url);
      sendCommand(command);
      context.newContext();
      checkForPageLoad();
      context.waitForLoadEvent();
      // wait for everything to be ready by fetching the doc.
      getDocument();
    } finally {
      context.eventsLock().unlock();
    }
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
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Runtime.evaluate");
      cmd.put("params",
              new JSONObject().put("expression", "document.title;").put("returnByValue", true));
      JSONObject response = sendCommand(cmd);
      return cast(response);
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
    /*String title = (String) executeScript("var state = document.title; return state",new JSONArray());
    return title;*/
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


  public WebDriver.TargetLocator switchTo() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public WebDriver.Navigation navigate() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public ResolverConfiguration.Options manage() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // TODO freynaud fix the element swapping.
  public Object executeScript(String script, JSONArray args) {

    try {
      RemoteWebElement document = getDocument();
      RemoteWebElement window = context.getWindow();
      JSONObject cmd = new JSONObject();

      JSONArray arguments = new JSONArray();
      int nbParam = args.length();
      for (int i = 0; i < nbParam; i++) {
        Object arg = args.get(i);
        if (arg instanceof JSONObject) {
          JSONObject jsonArg = (JSONObject) arg;
          if (jsonArg.optString("ELEMENT") != null) {
            // TODO use driver factory to check the  pageId
            NodeId n = new NodeId(Integer.parseInt(jsonArg.optString("ELEMENT").split("_")[1]));
            RemoteWebElement rwep = new RemoteWebElement(n, this);
            arguments.put(new JSONObject().put("objectId", rwep.getRemoteObject().getId()));
          }
        } else if (arg instanceof JSONArray) {
          JSONArray jsonArr = (JSONArray) arg;
          // maybe by executing some JS returning the array, and using that result
          // as a param ?
          throw new WebDriverException("no support argument = array.");
        } else {
          arguments.put(new JSONObject().put("value", arg));
        }

      }

      if (!context.isOnMainFrame()) {
        arguments.put(new JSONObject().put("objectId", document.getRemoteObject().getId()));
        arguments.put(new JSONObject().put("objectId", window.getRemoteObject().getId()));

        script = script.replace(" document", " arguments[" + nbParam + "]");
        script = script.replace(" window", "  arguments[" + (nbParam + 1) + "]");

      }

      cmd.put("method", "Runtime.callFunctionOn");
      cmd.put(
          "params",
          new JSONObject().put("objectId", document.getRemoteObject().getId())
              .put("functionDeclaration", "(function() { " + script + "})")
              .put("arguments", arguments)
              .put("returnByValue", false));
      JSONObject response = sendCommand(cmd);
      checkForJSErrors(response);
      Object o = cast(response);
      return o;
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }

  }

  private void checkForJSErrors(JSONObject response) throws JSONException {
    if (response.optBoolean("wasThrown")) {
      JSONObject details = response.getJSONObject("result");
      String desc = details.optString("description");
      throw new WebDriverException("JS error :" + desc);
    }
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

  private void flagPageLoaded() {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Runtime.evaluate");
      cmd.put("params",
              new JSONObject().put("expression", "window.top.iosdriver='" + context.getId() + "'"));
      sendCommand(cmd);
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }

  }

  public String getLoadedFlag() {
    JSONObject cmd = new JSONObject();
    try {
      cmd.put("method", "Runtime.evaluate");
      cmd.put("params", new JSONObject().put("expression", "window.top.iosdriver"));
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
    JSONObject response = sendCommand(cmd);
    return cast(response);
  }

  public boolean isReady() {
    return "complete".equals(getDocumentReadyState());
  }

  private String getDocumentReadyState() {
    String state = null;
    try {
      state = (String) executeScript("var state = document.readyState; return state",
                                     new JSONArray());
    } catch (Exception e) {
      // Arguments should belong to the same JavaScript world as the target object.
      System.err.println("error, reseting because " + e.getMessage());
      context.reset();
      return "unknown";
    }
    return state;
  }

  public void checkForPageLoad() {
    // a new page appeared.
    /*String id = getLoadedFlag();
    //System.out.println("on a page with id =" + id + " - " + context.getId());
    if (!context.getId().equals(id)) {

      long
          timeout = getTimeout();

      long deadline = System.currentTimeMillis() + timeout;
      //context.newContext();

      /*for (int i=0;i<100;i++){
        try {
          retrieveDocument().findElementByCSSSelector("#v4-1");
          //System.out.println(doc.getRemoteObject().call(".readyState"));
          } catch (Exception e) {
          System.err.println(e.getMessage()); //To change body of catch statement use File | Settings | File Templates.
        }
      }
      flagPageLoaded();
        */
    //System.out.println("on a page with id =" + getLoadedFlag());
    //}
  }

  private String getMainPageReadyState() {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Runtime.evaluate");
      cmd.put("params",
              new JSONObject().put("expression", "document.readyState;")
                  .put("returnByValue", true));
      JSONObject response = sendCommand(cmd);
      return cast(response);
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }

  private long getTimeout() {
    if (session == null) {
      return defaultPageLoadTimeoutInMs;
    } else {
      long timeout =
          (Long) session.configure(WebDriverLikeCommand.URL)
              .opt("page load", defaultPageLoadTimeoutInMs);
      if (timeout < 0) {
        return defaultPageLoadTimeoutInMs;
      }
      return timeout;

    }
  }

  @Override
  public void onMessage(IOSMessage message) {
    // a page was loaded.
    if (message instanceof ApplicationSentListingMessage) {
      /*ApplicationSentListingMessage m = (ApplicationSentListingMessage) message;
      // is it for this window ?
      String flag = getLoadedFlag();
      //System.out.println("GOT FLAG"+flag+" AT "+System.currentTimeMillis());
      if (context.getId().equals(getLoadedFlag())) {
        return;
      } else {
        //System.out.println("NEW PAGE EVENT AT "+System.currentTimeMillis());
        newPage = true;
        flagPageLoaded();
        context.newContext();
      } */
    }

    if (message instanceof ApplicationDataMessage) {
      ApplicationDataMessage m = (ApplicationDataMessage) message;
      EventFactory EventFactory = new EventFactory();
      Event e = EventFactory.createEvent(m.getMessage());
      if ((e instanceof ChildIframeInserted || e instanceof ChildNodeRemoved)) {
        context.domHasChanged(e);
      }
      if ("Page.frameDetached".equals(m.getMessage().optString("method"))) {
        context.frameDied(m.getMessage());
      }
      if ("Page.loadEventFired".equals(m.getMessage().optString("method"))) {
        context.signallNewPageLoadRecieved();
      }
      if ("Profiler.resetProfiles".equals(m.getMessage().optString("method"))) {

      }
    }
  }

  public DOMContext getContext() {
    return context;
  }

  public void back() throws JSONException {
    try {
      String f = "(function() { var f=" + IosAtoms.BACK + "();})()";
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Runtime.evaluate");
      cmd.put("params",
              new JSONObject().put("expression", f).put("returnByValue", true));
      JSONObject response = sendCommand(cmd);
      cast(response);
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }

  public void refresh() throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Page.reload");
    JSONObject response = sendCommand(cmd);
  }

  public void forward() throws Exception {
    try {
      String f = "(function() { var f=" + IosAtoms.FORWARD + "();})()";
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Runtime.evaluate");
      cmd.put("params",
              new JSONObject().put("expression", f).put("returnByValue", true));
      JSONObject response = sendCommand(cmd);
      cast(response);
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }


  public void highlightNode(NodeId nodeId) {
    sendCommand(DOM.highlightNode(nodeId));
  }
}
