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
package org.uiautomation.ios;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class IOSCapabilities {

  // UIAutomation properties called from instuments
  // UIAAplication.bundleID();
  public static final String UI_BUNDLE_ID = "bundleid";
  // UIATarget.systemName();
  public static final String UI_SYSTEM_NAME = "systemName";
  // UIATarget.systemVersion();
  public static final String UI_SDK_VERSION = "sdkVersion";
  // UIATarget.name();
  public static final String UI_NAME = "name";
  // UIAAplication.bundleVersion();
  public static final String UI_BUNDLE_VERSION = "bundleVersion";
  // UIAAplication.version();
  public static final String UI_VERSION = "version";


  // plist + envt variable
  public static final String DEVICE = "device";
  public static final String IOS_SWITCHES = "ios.switches";
  public static final String LANGUAGE = "language";
  public static final String SUPPORTED_LANGUAGES = "supportedLanguages";
  public static final String LOCALE = "locale";
  public static final String AUT = "aut";
  public static final String TIME_HACK = "timeHack";

  public static final String BUNDLE_VERSION = "CFBundleVersion";
  public static final String BUNDLE_ID = "CFBundleIdentifier";
  public static final String BUNDLE_SHORT_VERSION = "CFBundleShortVersionString";
  public static final String BUNDLE_DISPLAY_NAME = "CFBundleDisplayName";
  public static final String BUNDLE_NAME = "CFBundleName";
  public static final String DEVICE_FAMILLY = "UIDeviceFamily";
  // http://developer.apple.com/library/ios/#documentation/general/Reference/InfoPlistKeyReference/Articles/iPhoneOSKeys.html
  public static final String ICON = "CFBundleIconFile";

  public static final String MAGIC_PREFIX = "plist_";


  private final Map<String, Object> raw = new HashMap<String, Object>();

  public static IOSCapabilities iphone(String bundleName, String bundleVersion) {
    IOSCapabilities res = new IOSCapabilities();
    res.setCapability(DEVICE, IOSDevice.iPhoneSimulator);
    res.setCapability(LANGUAGE, "en");
    res.setCapability(LOCALE, "en_GB");
    res.setCapability(BUNDLE_NAME, bundleName);
    res.setCapability(BUNDLE_VERSION, bundleVersion);
    return res;
  }



  public static IOSCapabilities iphone(String bundleName) {
    IOSCapabilities res = new IOSCapabilities();
    res.setCapability(DEVICE, IOSDevice.iPhoneSimulator);
    res.setCapability(LANGUAGE, "en");
    res.setCapability(LOCALE, "en_GB");
    res.setCapability(BUNDLE_NAME, bundleName);
    return res;
  }

  public static IOSCapabilities ipad(String bundleName) {
    IOSCapabilities res = new IOSCapabilities();
    res.setCapability(DEVICE, IOSDevice.iPadSimulator);
    res.setCapability(LANGUAGE, "en");
    res.setCapability(LOCALE, "en_GB");
    res.setCapability(BUNDLE_NAME, bundleName);
    return res;
  }

  public IOSCapabilities() {
    setCapability(TIME_HACK, true);
  }

  public String getBundleName() {
    Object o = raw.get(BUNDLE_NAME);
    return ((String) o);
  }

  public String getBundleVersion() {
    Object o = raw.get(BUNDLE_VERSION);
    return ((String) o);
  }

  public IOSCapabilities(Map<String, Object> from) {
    raw.putAll(from);
  }

  public IOSCapabilities(JSONObject json) throws JSONException {
    Iterator<String> iter = json.keys();
    while (iter.hasNext()) {
      String key = iter.next();
      Object value = json.get(key);
      raw.put(key, decode(value));
    }
  }



  public void setCapability(String key, Object value) {
    raw.put(key, value);
  }


  private Object decode(Object o) throws JSONException {
    if (o instanceof JSONArray) {
      List<Object> res = new ArrayList<Object>();
      JSONArray array = (JSONArray) o;
      for (int i = 0; i < array.length(); i++) {
        Object r = array.get(i);
        res.add(decode(r));
      }
      return res;
    } else {
      return o;
    }
  }

  public Map<String, Object> getRawCapabilities() {
    return raw;
  }

  public IOSDevice getDeviceFromDeviceFamily() {
    JSONArray o = (JSONArray) raw.get(DEVICE_FAMILLY);
    if (o.length() != 1) {
      throw new IOSAutomationException(
          "Cannot find the capability for the app.Supported only 1, and got " + o);
    } else {
      try {
        int deviceId = o.getInt(0);
        IOSDevice res = IOSDevice.getFromFamilyCode(deviceId);
        return res;
      } catch (JSONException e) {
        throw new IOSAutomationException("wrong format for device family. Got " + o);
      }
    }
  }

  public IOSDevice getDevice() {
    Object o = raw.get(DEVICE);
    return IOSDevice.valueOf(o);
  }

  public String getSDKVersion() {
    Object o = raw.get(UI_SDK_VERSION);
    return ((String) o);
  }

  public String getApplication() {
    Object o = raw.get(AUT);
    return ((String) o);
  }

  public String getLocale() {
    Object o = raw.get(LOCALE);
    return ((String) o);
  }

  public String getLanguage() {
    Object o = raw.get(LANGUAGE);
    return ((String) o);

  }

  public void setDevice(IOSDevice device) {
    raw.put(DEVICE, device);

  }

  public void setSDKVersion(String sdkVersion) {
    raw.put(UI_SDK_VERSION, sdkVersion);
  }

  public void setLocale(String locale) {
    raw.put(LOCALE, locale);

  }

  public void setLanguage(String language) {
    raw.put(LANGUAGE, language);
  }

  public List<String> getExtraSwitches() {
    List<String> res = new ArrayList<String>();
    if (raw.get(IOS_SWITCHES) != null) {
      res.addAll(getList(IOS_SWITCHES));
    }
    return res;
  }

  public boolean isTimeHack() {
    Object o = raw.get(TIME_HACK);
    if (o == null) {
      return false;
    } else if (o instanceof Boolean) {
      return (Boolean) o;
    } else {
      return Boolean.parseBoolean((String) o);
    }
  }

  private List<String> getList(String key) {
    Object o = raw.get(key);
    if (o instanceof List<?>) {
      return (List<String>) o;
    } else if (o instanceof JSONArray) {
      JSONArray a = (JSONArray) o;
      List<String> res = new ArrayList<String>();

      for (int i = 0; i < a.length(); i++) {
        try {
          res.add(a.getString(i));
        } catch (JSONException e) {
          throw new IOSAutomationException(e);
        }
      }
      return res;
    }
    throw new ClassCastException("expected collection of string, got " + o.getClass());
  }

  public List<String> getSupportedLanguages() {
    return getList(SUPPORTED_LANGUAGES);
  }

  public void setSupportedLanguages(List<String> supportedLanguages) {
    raw.put(SUPPORTED_LANGUAGES, supportedLanguages);
  }



  public void setBundleName(String bundleName) {
    setCapability(BUNDLE_NAME, bundleName);
  }

  public Object getCapability(String key) {
    Object o = getRawCapabilities().get(key);
    if (o != null && o.equals(JSONObject.NULL)) {
      return null;
    } else {
      return o;
    }

  }
}
