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

package org.uiautomation.ios.server.application;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.server.utils.ZipUtils;
import org.uiautomation.ios.utils.PlistFileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.uiautomation.ios.IOSCapabilities.*;

// TODO freynaud create IOSApp vs Running App that has locale + language
public class APPIOSApplication {

  private static final Logger log = Logger.getLogger(APPIOSApplication.class.getName());

  private final JSONObject metadata;
  private final File app;
  private final List<LanguageDictionary> dictionaries = new ArrayList<LanguageDictionary>();

  /**
   * @param pathToApp
   * @throws WebDriverException
   */
  public APPIOSApplication(String pathToApp) {
    this.app = new File(pathToApp);
    if (!app.exists()) {
      throw new WebDriverException(pathToApp + "isn't an IOS app.");
    }
    loadAllContent();
    try {
      metadata = getFullPlist();
    } catch (Exception e) {
      throw new WebDriverException(
          "cannot load the metadata from the Info.plist file for " + pathToApp);
    }
  }

  public String toString() {
    return ".APP:" + getApplicationPath().getAbsolutePath();
  }

  /**
   * the content of the Info.plist for the app, as a json object.
   */
  public JSONObject getMetadata() {
    return metadata;
  }

  public List<String> getSupportedLanguagesCodes() {
    List<AppleLocale> list = getSupportedLanguages();
    List<String> res = new ArrayList<String>();
    for (AppleLocale app : list) {
      res.add(app.getLocale().toString());
    }
    return res;
  }

  /**
   * get the list of languages the application if localized to.
   */
  List<AppleLocale> getSupportedLanguages() {
    if (dictionaries.isEmpty()) {
      loadAllContent();
    }
    /*
     * Set<Localizable> res = new HashSet<Localizable>(); List<File> l10ns =
     * LanguageDictionary.getL10NFiles(app); for (File f : l10ns) { String name
     * = LanguageDictionary.extractLanguageName(f); res.add(new
     * LanguageDictionary(name).getLanguage()); } return new
     * ArrayList<Localizable>(res);
     */
    List<AppleLocale> res = new ArrayList<AppleLocale>();
    for (LanguageDictionary dict : dictionaries) {
      res.add(dict.getLanguage());
    }
    return res;
  }

  public AppleLocale getAppleLocaleFromLanguageCode(String languageCode) {
    if (getSupportedLanguages().isEmpty()) {
      return AppleLocale.emptyLocale(languageCode);
    }
    if (languageCode == null) {
      // default to english if none specified
      languageCode = "en";
    }
    for (AppleLocale loc : getSupportedLanguages()) {
      if (languageCode.equals(loc.getLocale().getLanguage())) {
        return loc;
      }
    }
    throw new WebDriverException("Cannot find AppleLocale for " + languageCode);
  }

  public LanguageDictionary getDictionary(String languageCode) throws WebDriverException {
    return getDictionary(getAppleLocaleFromLanguageCode(languageCode));

  }

  public LanguageDictionary getDictionary(AppleLocale language) throws WebDriverException {
    if (!language.exist()) {
      throw new WebDriverException("The application doesn't have any content files.The l10n "
                                   + "features cannot be used.");
    }
    for (LanguageDictionary dict : dictionaries) {
      if (dict.getLanguage() == language) {
        return dict;
      }
    }
    throw new WebDriverException("Cannot find dictionary for " + language);

  }

  /**
   * Load all the dictionaries for the application.
   */
  private void loadAllContent() throws WebDriverException {
    if (!dictionaries.isEmpty()) {
      throw new WebDriverException("Content already present.");
    }
    Map<String, LanguageDictionary> dicts = new HashMap<String, LanguageDictionary>();

    List<File> l10nFiles = LanguageDictionary.getL10NFiles(app);
    for (File f : l10nFiles) {
      String name = LanguageDictionary.extractLanguageName(f);
      LanguageDictionary res = dicts.get(name);
      if (res == null) {
        res = new LanguageDictionary(name);
        dicts.put(name, res);
      }
      try {
        // and load the content.
        JSONObject content = res.readContentFromBinaryFile(f);
        res.addJSONContent(content);
      } catch (Exception e) {
        throw new WebDriverException("error loading content for l10n", e);
      }

    }
    dictionaries.addAll(dicts.values());

  }

  public String translate(ContentResult res, AppleLocale language) throws WebDriverException {
    LanguageDictionary destinationLanguage = getDictionary(language);
    return destinationLanguage.translate(res);

  }


  public void addDictionary(LanguageDictionary dict) {
    dictionaries.add(dict);
  }

  public String getBundleId() {
    return getMetadata("CFBundleIdentifier");
  }


  public File getApplicationPath() {
    return app;
  }

  /**
   * the list of resources to publish via http.
   */
  public Map<String, String> getResources() {
    Map<String, String> resourceByResourceName = new HashMap<String, String>();
    String metadata =  getMetadata(ICON);
    if(metadata.equals("")){
      metadata = getFirstIconFile(BUNDLE_ICONS);
    }
    resourceByResourceName.put(ICON, metadata);
    return resourceByResourceName;
  }

  private String getFirstIconFile(String bundleIcons){
    if(!metadata.has(bundleIcons)){
      return "";
    }
    try{
      HashMap icons = (HashMap)metadata.get(bundleIcons);
      HashMap primaryIcon = (HashMap)icons.get("CFBundlePrimaryIcon");
      ArrayList iconFiles = (ArrayList)primaryIcon.get("CFBundleIconFiles");
      return iconFiles.get(0).toString();
    }
    catch (JSONException e) {
      throw new WebDriverException("property 'CFBundleIcons' can't be returned. " + e.getMessage(), e);
    }
  }

  private JSONObject getFullPlist() throws Exception {
    File plist = new File(app, "Info.plist");
    PlistFileUtils util = new PlistFileUtils(plist);
    return util.toJSON();
  }

  public String getMetadata(String key) {
    if (!metadata.has(key)) {
      return "";
      // throw new WebDriverException("no property " + key +
      // " for this app.");
    }
    try {
      return metadata.getString(key);
    } catch (JSONException e) {
      throw new WebDriverException("property " + key + " can't be returned. " + e.getMessage(), e);
    }
  }

  public List<Integer> getDeviceFamily() {
    try {
      JSONArray array = metadata.getJSONArray(DEVICE_FAMILLY);
      List<Integer> res = new ArrayList<Integer>();
      for (int i = 0; i < array.length(); i++) {
        res.add(array.getInt(i));
      }
      return res;
    } catch (JSONException e) {
      throw new WebDriverException("Cannot load device family", e);
    }
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((app == null) ? 0 : app.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    APPIOSApplication other = (APPIOSApplication) obj;
    if (app == null) {
      if (other.app != null) {
        return false;
      }
    } else if (!app.equals(other.app)) {
      return false;
    }
    return true;
  }

  public static APPIOSApplication findSafariLocation(File xcodeInstall, String sdkVersion) {
    File app = new File(xcodeInstall,
                        "/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator"
                        + sdkVersion
                        + ".sdk/Applications/MobileSafari.app");
    if (!app.exists()) {
      throw new WebDriverException(app + " should be the safari app, but doesn't exist.");
    }
    return new APPIOSApplication(app.getAbsolutePath());
  }

  public void setDefaultDevice(DeviceType device) {

    try {
      File plist = new File(app, "Info.plist");
      NSDictionary root = (NSDictionary) BinaryPropertyListParser.parse(new FileInputStream(plist));

      NSArray devices = (NSArray) root.objectForKey("UIDeviceFamily");
      int length = devices.getArray().length;
      if (length == 1) {
        return;
      }

      NSArray rearrangedArray = new NSArray(length);
      NSNumber last = null;
      int index = 0;
      for (int i = 0; i < length; i++) {
        NSNumber d = (NSNumber) devices.objectAtIndex(i);
        if (d.intValue() == device.getDeviceFamily()) {
          last = d;
        } else {
          rearrangedArray.setValue(index, d);
          index++;
        }
      }
      if (last == null) {
        throw new WebDriverException(
            "Cannot find device " + device + " in the supported device list.");
      }
      rearrangedArray.setValue(index, last);
      root.put("UIDeviceFamily", rearrangedArray);
      BinaryPropertyListWriter.write(plist, root);
    } catch (Exception e) {
      throw new WebDriverException("Cannot change the default device for the app." + e.getMessage(),
                                   e);
    }

  }

  public IOSCapabilities getCapabilities() {
    IOSCapabilities cap = new IOSCapabilities();
    cap.setSupportedLanguages(getSupportedLanguagesCodes());
    cap.setCapability("applicationPath", getApplicationPath().getAbsoluteFile());
    List<DeviceType> supported = getSupportedDevices();

    if (supported.contains(DeviceType.iphone)) {
      cap.setDevice(DeviceType.iphone);
    } else {
      cap.setDevice(DeviceType.ipad);
    }

    if (this instanceof IPAApplication) {
      cap.setCapability(IOSCapabilities.SIMULATOR, false);
    } else {
      cap.setCapability(IOSCapabilities.SIMULATOR, true);
    }

    cap.setCapability(IOSCapabilities.SUPPORTED_DEVICES, supported);
    for (Iterator iterator = getMetadata().keys(); iterator.hasNext(); ) {
      String key = (String) iterator.next();

      try {
        Object value = getMetadata().get(key);
        cap.setCapability(key, value);
      } catch (JSONException e) {
        throw new WebDriverException("cannot get metadata", e);
      }
    }
    return cap;
  }

  public static boolean canRun(IOSCapabilities desiredCapabilities, IOSCapabilities appCapability) {
    if (desiredCapabilities.isSimulator() != null &&
        desiredCapabilities.isSimulator() != appCapability.isSimulator()) {
      return false;
    }
    if (desiredCapabilities.getBundleName() == null) {
      throw new WebDriverException("you need to specify the bundle to test.");
    }
    String desired = desiredCapabilities.getBundleName();

    String bundleName = (String) appCapability.getCapability(IOSCapabilities.BUNDLE_NAME);
    String displayName = (String) appCapability.getCapability(IOSCapabilities.BUNDLE_DISPLAY_NAME);
    String name = bundleName != null ? bundleName : displayName;

    if (!desired.equals(name)) {
      return false;
    }

    if (desiredCapabilities.getBundleVersion() != null && !desiredCapabilities.getBundleVersion()
        .equals(appCapability.getBundleVersion())) {
      return false;
    }

    if (desiredCapabilities.getDevice() == null) {
      throw new WebDriverException("you need to specify the device.");
    }
    if (!(appCapability.getSupportedDevices()
              .contains(desiredCapabilities.getDevice()))) {
      return false;
    }

    // check any extra capability starting with plist_
    for (String key : desiredCapabilities.getRawCapabilities().keySet()) {
      if (key.startsWith(IOSCapabilities.MAGIC_PREFIX)) {
        String realKey = key.replace(MAGIC_PREFIX, "");
        if (!desiredCapabilities.getRawCapabilities().get(key)
            .equals(appCapability.getRawCapabilities().get(realKey))) {
          return false;
        }
      }
    }
    String l = desiredCapabilities.getLanguage();

    if (appCapability.getSupportedLanguages().isEmpty()) {
      log.info(
          "The application doesn't have any content files."
          + "The localization related features won't be availabled.");
    } else if (l != null && !appCapability.getSupportedLanguages().contains(l)) {
      throw new SessionNotCreatedException(
          "Language requested, " + l + " ,isn't supported.Supported are : "
          + appCapability.getSupportedLanguages());
    }
    return true;
  }

  public String getBundleVersion() {
    return getMetadata(IOSCapabilities.BUNDLE_VERSION);
  }

  public String getApplicationName() {
    String name = getMetadata(IOSCapabilities.BUNDLE_NAME);
    String displayName = getMetadata(IOSCapabilities.BUNDLE_DISPLAY_NAME);
    return (name != null) && ! name.trim().isEmpty() ? name : displayName;

  }

  public List<DeviceType> getSupportedDevices() {
    List<DeviceType> families = new ArrayList<DeviceType>();
    String s = (String) getMetadata(IOSCapabilities.DEVICE_FAMILLY);
    try {
      JSONArray ar = new JSONArray(s);
      for (int i = 0; i < ar.length(); i++) {
        int f = ar.getInt(i);
        if (f == 1) {
          families.add(DeviceType.iphone);
          families.add(DeviceType.ipod);
        } else {
          families.add(DeviceType.ipad);
        }
      }
      return families;

    } catch (JSONException e) {
      throw new WebDriverException(e);
    }

  }

  public boolean isSimulator() {
    return "iphonesimulator".equals(getMetadata("DTPlatformName"));
  }

  public IOSRunningApplication createInstance(String language) {
    return new IOSRunningApplication(language, this);
  }

  public static APPIOSApplication createFrom(File app) {
    if (app == null || !app.exists()) {
      return null;
    } else if (app.getAbsolutePath().endsWith(".ipa")) {
      return IPAApplication.createFrom(app);
    } else if (app.getAbsolutePath().endsWith(".app")) {
      return new APPIOSApplication(app.getAbsolutePath());
    } else {
      return null;
    }
  }


}
