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

package org.uiautomation.ios.webInspector.DOM;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAWebView;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.LabelCriteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.OrCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.context.BaseWebInspector;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.AppleLocale;
import org.uiautomation.ios.server.application.ContentResult;

import java.util.List;
import java.util.logging.Logger;

public class RemoteWebNativeBackedElement extends RemoteWebElement {


  private static final Logger log = Logger.getLogger(RemoteWebNativeBackedElement.class.getName());
  private final ServerSideSession session;
  private final RemoteUIADriver nativeDriver;

  public RemoteWebNativeBackedElement(NodeId id, BaseWebInspector inspector,
                                      ServerSideSession session) {
    super(id, inspector);
    this.session = session;
    this.nativeDriver = session.getNativeDriver();
  }

  public void nativeClick() {
    try {
      ((JavascriptExecutor) nativeDriver).executeScript(getNativeElementClickOnIt());
      inspector.checkForPageLoad();
    } catch (Exception e) {
      throw new WebDriverException(e);
    }
  }

  private String getNativeElementClickOnIt() throws Exception {
    // web stuff.
    scrollIntoViewIfNeeded();
    Point po = findPosition();

    Dimension dim = inspector.getSize();
    int webPageWidth = inspector.getInnerWidth();
    if (dim.getWidth() != webPageWidth) {
      log.fine("BUG : dim.getWidth()!=webPageWidth");
    }

    Criteria c = new TypeCriteria(UIAWebView.class);
    String json = c.stringify().toString();
    StringBuilder script = new StringBuilder();
    script.append("var root = UIAutomation.cache.get('1');");
    script.append("var webview = root.element(-1," + json + ");");
    script.append("var webviewSize = webview.rect();");
    script.append("var ratio = webviewSize.size.width / " + dim.getWidth() + ";");
    int top = po.getY();
    int left = po.getX();
    script.append("var top = (" + top + "*ratio )+1;");
    script.append("var left = (" + left + "*ratio)+1;");

    script.append("var x = left;");
    boolean ipad = session.getCapabilities().getDevice() == Device.ipad;
    if (ipad) {
      // for ipad, the adress bar h is fixed @ 96px.
      script.append("var y = top+96;");
    } else {
      AppleLocale current = session.getApplication().getCurrentLanguage();
      List<ContentResult>
          results =
          session.getApplication().getDictionary(current).getPotentialMatches("Address");
      if (results.size() != 1) {
        log.warning("translation returned " + results.size());
      }
      ContentResult result = results.get(0);
      String addressL10ned = result.getL10nFormatted();
      Criteria
          c2 =
          new AndCriteria(new TypeCriteria(UIAElement.class), new NameCriteria(addressL10ned),
                          new LabelCriteria(addressL10ned));
      script.append("var addressBar = root.element(-1," + c2.stringify().toString() + ");");
      script.append("var addressBarSize = addressBar.rect();");
      script.append("var delta = addressBarSize.origin.y +39;");
      script.append("if (delta<20){delta=20;};");
      script.append("var y = top+delta;");
    }
    script.append("UIATarget.localTarget().tap({'x':x,'y':y});");
    return script.toString();

  }

  // TODO freynaud use keyboard.js bot.Keyboard.prototype.moveCursor = function(element)
  private String getNativeElementClickOnItAndTypeUsingKeyboardScript(String value)
      throws Exception {

    StringBuilder script = new StringBuilder();
    script.append(getNativeElementClickOnIt());
    script.append("var keyboard = UIAutomation.cache.get('1').keyboard();");
    script.append("keyboard.typeString('" + value + "');");
    Criteria iPhone = new NameCriteria("Done");
    Criteria iPad = new NameCriteria("Hide keyboard");

    Criteria c3 = new OrCriteria(iPad, iPhone);
    // TODO freynaud create keyboard.hide();
    script.append("root.element(-1," + c3.stringify().toString() + ").tap();");

    return script.toString();

  }


  public void setValueNative(String value) throws Exception {
    ((JavascriptExecutor) nativeDriver)
        .executeScript(getNativeElementClickOnItAndTypeUsingKeyboardScript(value));
  }


}
