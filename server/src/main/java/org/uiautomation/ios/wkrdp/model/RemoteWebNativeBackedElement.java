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

package org.uiautomation.ios.wkrdp.model;

import org.openqa.selenium.*;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAWebView;
import org.uiautomation.ios.UIAModels.predicate.*;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.context.BaseWebInspector;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.ContentResult;
import org.uiautomation.ios.utils.XPath2Engine;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RemoteWebNativeBackedElement extends RemoteWebElement {


  private static final Logger log = Logger.getLogger(RemoteWebNativeBackedElement.class.getName());
  private final ServerSideSession session;
  private final RemoteIOSDriver nativeDriver;
  private final List<Character> specialKeys = new ArrayList<Character>() {{
    this.add(Keys.DELETE.toString().charAt(0));
    this.add(Keys.ENTER.toString().charAt(0));
    this.add(Keys.RETURN.toString().charAt(0));
    this.add(Keys.SHIFT.toString().charAt(0));
  }};

  public RemoteWebNativeBackedElement(NodeId id, BaseWebInspector inspector,
                                      ServerSideSession session) {
    super(id, inspector);
    this.session = session;
    this.nativeDriver = session.getNativeDriver();
  }

  private static String normalizeDateValue(String value) {
    // convert MM/DD/YYYY to YYYY-MM-DD
    int sep1 = value.indexOf('/');
    int sep2 = value.lastIndexOf('/');
    if (sep1 == -1 || sep2 == -1)
      return value;

    String mm = value.substring(0, sep1);
    String dd = value.substring(sep1 + 1, sep2);
    String yyyy = value.substring(sep2 + 1);

    return yyyy + '-' + to2CharDateDigit(mm) + '-' + to2CharDateDigit(dd);
  }

  private static String to2CharDateDigit(String text) {
    return (text.length() == 1) ? '0' + text : text;
  }

  private boolean isSafari() {
    return session.getApplication().isSafari();
  }

  public void nativeClick() {

    if ("option".equalsIgnoreCase(getTagName())) {
      click();

    } else {
      try {
        ((JavascriptExecutor) nativeDriver).executeScript(getNativeElementClickOnIt());
        getInspector().checkForPageLoad();
      } catch (Exception e) {
        throw new WebDriverException(e);
      }
    }

  }

  @Override
  public Point getLocation()
      throws Exception {
    // web stuff.
    //scrollIntoViewIfNeeded();
    Point po = findPosition();

    Dimension dim = getInspector().getSize();
    int webPageWidth = getInspector().getInnerWidth();
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
    boolean ipad = session.getCapabilities().getDevice() == DeviceType.ipad;

    if (isSafari()) {
      if (ipad) {
        // for ipad, the adress bar h is fixed @ 96px.
        script.append("var y = top+96;");
      } else {

        List<ContentResult>
            results =
            session.getApplication().getCurrentDictionary().getPotentialMatches("Address");
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
    } else {
      Criteria wv = new TypeCriteria(UIAWebView.class);
      script.append("var webview = root.element(-1," + wv.stringify().toString() + ");");
      script.append("var size = webview.rect();");
      script.append("var offsetY = size.origin.y;");
      // UIAWebView.y
      script.append("var y = top+offsetY;");
    }
    script.append("return new Array(parseInt(x), parseInt(y));");

    Object response = ((JavascriptExecutor) nativeDriver).executeScript(String.valueOf(script));

    int x = ((ArrayList<Long>) response).get(0).intValue();
    int y = ((ArrayList<Long>) response).get(1).intValue();

    return new Point(x, y);
  }

  private String getNativeElementClickOnIt() throws Exception {
    // web stuff.
    scrollIntoViewIfNeeded();
    Point location = getLocation();
    String script = "UIATarget.localTarget().tap({'x':x_coord,'y':y_coord});";
    script = script.replace("x_coord", String.valueOf(location.getX()))
        .replace("y_coord", String.valueOf(location.getY()));
    return script;
  }

  private String getKeyboardTypeStringSegement(String value) {
    StringBuilder script = new StringBuilder();
    script.append("keyboard.typeString('");
    // got to love java...
    // first replacing a \ (backslash) with \\ (double backslash)
    // and then ' (single quote) with \' (backslash, single quote)
    script.append(value.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'"));
    script.append("');");
    return script.toString();
  }

  private String getReferenceToTapByXpath(XPath2Engine xPath2Engine, String xpath) {
    StringBuilder script = new StringBuilder();
    script.append("UIAutomation.cache.get(");
    script.append((String) xPath2Engine.findElementByXpath(xpath).get("ELEMENT"));
    script.append(", false).tap();");
    return script.toString();
  }

  // TODO freynaud use keyboard.js bot.Keyboard.prototype.moveCursor = function(element)
  private String getNativeElementClickOnItAndTypeUsingKeyboardScript(String value)
      throws Exception {


    StringBuilder script = new StringBuilder();
    script.append("var keyboard = UIAutomation.cache.get('1').keyboard();");

    Boolean keyboardResigned = false;

    StringBuilder current = new StringBuilder();
    XPath2Engine xpathEngine = null;
    for (int i = 0; i < value.length(); i++) {
      int idx = specialKeys.indexOf(value.charAt(i));
      if (idx >= 0) {
        if (xpathEngine == null) {
          xpathEngine = XPath2Engine.getXpath2Engine(nativeDriver);
        }
        if (current.length() > 0) {
          script.append(getKeyboardTypeStringSegement(current.toString()));
          current = new StringBuilder();
        }
        switch (idx) {
          case 0:
            // DELETE
            // TODO, i don't like this xpath... should find the key in a better way
            // (like keyboard.shift)
            script.append(getReferenceToTapByXpath(xpathEngine, "//UIAKeyboard/UIAKey[" +
                (nativeDriver.getCapabilities().getDevice() == DeviceType.ipad ?
                    "11]" : "last()-3]")
            ));
            break;
          case 1:
          case 2:
            // ENTER / RETURN
            // TODO another smelly xpath.
            script.append(getReferenceToTapByXpath(xpathEngine, "//UIAKeyboard/UIAButton[" +
                (nativeDriver.getCapabilities().getDevice() == DeviceType.ipad ?
                    "1]" : "2]")
            ));
            keyboardResigned = true;
            break;
          case 3:
            // SHIFT
            script.append("keyboard.shift();");
            break;
          default:
            throw new RuntimeException("Special key found in the list but not taken care of??");
        }
      } else {
        current.append(value.charAt(i));
      }
    }
    if (current.length() > 0) {
      script.append(getKeyboardTypeStringSegement(current.toString()));
    }

    if (!keyboardResigned) {
      script.append("keyboard.hide();");
    }
    return script.toString();
  }

  public void setValueNative(String value) throws Exception {
    String type = getAttribute("type");
    if ("date".equalsIgnoreCase(type)) {
      value = normalizeDateValue(value);
      setValueAtoms(value);
      return;
    }
    // iphone on telephone inputs only shows the keypad keyboard.
    if ("tel".equalsIgnoreCase(type) && nativeDriver.getCapabilities().getDevice() == DeviceType.iphone) {
      value = replaceLettersWithNumbersKeypad(value,
          (String) nativeDriver.getCapabilities().getCapability(IOSCapabilities.LOCALE));
    }
    ((JavascriptExecutor) nativeDriver)
        .executeScript(getNativeElementClickOnIt());
    Thread.sleep(750);
    setCursorAtTheEnd();
    ((JavascriptExecutor) nativeDriver)
        .executeScript(getNativeElementClickOnItAndTypeUsingKeyboardScript(value));
  }

  // TODO actually handle more locales
  private String replaceLettersWithNumbersKeypad(String str, String locale) {
    if (locale.toLowerCase().startsWith("en")) {
      return str.replaceAll("[AaBbCc]", "2").replaceAll("[DdEeFf]", "3").replaceAll("[GgHhIi]", "4")
          .replaceAll("[JjKkLl]", "5").replaceAll("[MmNnOo]", "6").replaceAll("[PpQqRrSs]", "7")
          .replaceAll("[TtUuVv]", "8").replaceAll("[WwXxYyZz]", "9").replaceAll("-", "");
    }
    return str.replaceAll("-", "");
  }
}
