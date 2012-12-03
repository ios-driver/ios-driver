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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.communication.device.DeviceVariation;

public class IOSCapabilities extends DesiredCapabilities {

  // UIAutomation properties called from instuments
  // UIAAplication.bundleID();
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
  public static final String VARIATION = "variation";
  public static final String SIMULATOR = "simulator";

  public static final String IOS_SWITCHES = "ios.switches";
  public static final String LANGUAGE = "language";
  public static final String SUPPORTED_LANGUAGES = "supportedLanguages";
  public static final String SUPPORTED_DEVICES = "supportedDevices";
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

  // private final Map<String, Object> raw = new HashMap<String, Object>();

  public static IOSCapabilities iphone(String bundleName, String bundleVersion) {
    IOSCapabilities res = new IOSCapabilities();
    res.setCapability(DEVICE, Device.iphone);
    res.setCapability(LANGUAGE, "en");
    res.setCapability(LOCALE, "en_GB");
    res.setCapability(BUNDLE_NAME, bundleName);
    res.setCapability(BUNDLE_VERSION, bundleVersion);
    return res;
  }

  public static IOSCapabilities mobileSafariIpad() {
    return IOSCapabilities.ipad("Safari");
  }

  public static IOSCapabilities iphone(String bundleName) {
    IOSCapabilities res = new IOSCapabilities();
    res.setCapability(DEVICE, Device.iphone);
    res.setCapability(LANGUAGE, "en");
    res.setCapability(LOCALE, "en_GB");
    res.setCapability(BUNDLE_NAME, bundleName);
    return res;
  }

  public static IOSCapabilities ipad(String bundleName) {
    IOSCapabilities res = new IOSCapabilities();
    res.setCapability(DEVICE, Device.ipad);
    res.setCapability(LANGUAGE, "en");
    res.setCapability(LOCALE, "en_GB");
    res.setCapability(BUNDLE_NAME, bundleName);
    return res;
  }

  public IOSCapabilities() {
    setCapability(TIME_HACK, false);
    setCapability(VARIATION, DeviceVariation.Regular);
    setCapability(SIMULATOR, true);
  }

  /*
   * public IOSCapabilities(Map<String, Object> from) { for (String key :
   * from.keySet()) { setCapability(key, from.get(key)); } }
   */

  public String getBundleId() {
    Object o = asMap().get(BUNDLE_ID);
    return ((String) o);
  }

  public String getBundleName() {
    Object o = asMap().get(BUNDLE_NAME);
    return ((String) o);
  }

  public String getBundleVersion() {
    Object o = asMap().get(BUNDLE_VERSION);
    return ((String) o);
  }

  public IOSCapabilities(JSONObject json) throws JSONException {
    Iterator<String> iter = json.keys();
    while (iter.hasNext()) {
      String key = iter.next();
      Object value = json.get(key);
      setCapability(key, decode(value));
    }
  }

  public IOSCapabilities(Map<String, ?> from) {
    for (String key : from.keySet()) {
      setCapability(key, from.get(key));
    }
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
    return (Map<String, Object>) asMap();
  }

  public void setSupportedDevices(List<Device> devices) {
    if (devices.isEmpty()) {
      throw new WebDriverException("your app need to support at least 1  device.");
    }
    setCapability(SUPPORTED_DEVICES, devices);
  }

  public List<Device> getSupportedDevicesFromDeviceFamily() {
    JSONArray o = (JSONArray) asMap().get(DEVICE_FAMILLY);
    List<Device> devices = new ArrayList<Device>();
    for (int i = 0; i < o.length(); i++) {
      try {
        devices.add(Device.getFromFamilyCode(o.getInt(i)));
      } catch (JSONException e) {
        throw new WebDriverException(o.toString() + " but should contain only 1 or 2.");
      }
    }
    return devices;
  }

  public Device getDevice() {
    Object o = getCapability(DEVICE);
    return Device.valueOf(o);
  }

  public String getSDKVersion() {
    Object o = getCapability(UI_SDK_VERSION);
    return ((String) o);
  }

  public String getApplication() {
    Object o = getCapability(AUT);
    return ((String) o);
  }

  public String getLocale() {
    Object o = getCapability(LOCALE);
    return ((String) o);
  }

  public String getLanguage() {
    Object o = getCapability(LANGUAGE);
    return ((String) o);

  }

  public void setDevice(Device device) {
    setCapability(DEVICE, device);
  }

  public void setSDKVersion(String sdkVersion) {
    setCapability(UI_SDK_VERSION, sdkVersion);
  }

  public void setLocale(String locale) {
    setCapability(LOCALE, locale);

  }

  public void setLanguage(String language) {
    setCapability(LANGUAGE, language);
  }

  public List<String> getExtraSwitches() {
    List<String> res = new ArrayList<String>();
    if (getCapability(IOS_SWITCHES) != null) {
      res.addAll(getList(IOS_SWITCHES));
    }
    return res;
  }

  public boolean isTimeHack() {
    Object o = getCapability(TIME_HACK);
    if (o == null) {
      return false;
    } else if (o instanceof Boolean) {
      return (Boolean) o;
    } else {
      return Boolean.parseBoolean((String) o);
    }
  }

  private List<String> getList(String key) {
    Object o = getCapability(key);
    if (o instanceof List<?>) {
      return (List<String>) o;
    } else if (o instanceof JSONArray) {
      JSONArray a = (JSONArray) o;
      List<String> res = new ArrayList<String>();

      for (int i = 0; i < a.length(); i++) {
        try {
          res.add(a.getString(i));
        } catch (JSONException e) {
          throw new WebDriverException(e);
        }
      }
      return res;
    }
    throw new ClassCastException("expected collection of string, got " + o.getClass());
  }

  public List<String> getSupportedLanguages() {
    return getList(SUPPORTED_LANGUAGES);
  }

  public List<String> getSupportedDevices() {
    return getList(SUPPORTED_DEVICES);
  }

  public void setSupportedLanguages(List<String> supportedLanguages) {
    setCapability(SUPPORTED_LANGUAGES, supportedLanguages);
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

  public void setDeviceVariation(DeviceVariation variation) {
    setCapability(VARIATION, variation);
  }

  public DeviceVariation getDeviceVariation() {
    Object o = getCapability(VARIATION);
    return DeviceVariation.valueOf(o);
  }

}
