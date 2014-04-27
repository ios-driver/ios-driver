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

package org.uiautomation.ios.server.application;

import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.google.common.collect.ImmutableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.utils.IOSVersion;
import org.uiautomation.ios.utils.PlistFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.uiautomation.ios.IOSCapabilities.*;

// TODO freynaud create IOSApp vs Running App that has locale + language
public class APPIOSApplication {

  public static void main(String[] args) throws Exception {
    showAppData("MobileSafari6.1", APPIOSApplication.findSafariApp(ClassicCommands.getXCodeInstall(), "6.1"));
    showAppData("MobileSafari7.0", APPIOSApplication.findSafariApp(ClassicCommands.getXCodeInstall(), "7.0"));
  }

  private static void showAppData(String label, APPIOSApplication app) throws JSONException {
    System.out.println(label + ':');
    System.out.println("  path: " + app.getApplicationPath().getAbsolutePath());
    System.out.println("  metadata: " + app.getMetadata().toString(2));
  }

  //

  private static final Logger log = Logger.getLogger(APPIOSApplication.class.getName());

  private final JSONObject metadata;
  private final File app;
  private final ImmutableList<LanguageDictionary> dictionaries;
  private final ImmutableList<AppleLanguage> languages;

  /**
   * @param pathToApp
   * @throws WebDriverException
   */
  public APPIOSApplication(String pathToApp) {
    if (pathToApp == null) {
      metadata = null;
      app = null;
      dictionaries = null;
      languages = null;
      return;
    }
    this.app = new File(pathToApp);
    if (!app.exists()) {
      throw new WebDriverException(pathToApp + "isn't an IOS app.");
    }
    dictionaries = loadDictionaries();
    languages = loadLanguages();
    try {
      metadata = getFullPlist();
    } catch (Exception e) {
      throw new WebDriverException(
          "cannot load the metadata from the Info.plist file for " + pathToApp);
    }
  }

  /**
   * @return a String of the form "CFBundleName={name},CFBundleVersion={version},app-path"
   */
  @Override
  public String toString() {
    StringBuilder info = new StringBuilder();
    String name = getMetadata(IOSCapabilities.BUNDLE_NAME).isEmpty()
       ? getMetadata(IOSCapabilities.BUNDLE_DISPLAY_NAME)
       : getMetadata(IOSCapabilities.BUNDLE_NAME);
    info.append(String.format("CFBundleName=%s", name));
    String version = getMetadata(IOSCapabilities.BUNDLE_VERSION);
    if (version != null && !version.isEmpty()) {
      info.append(String.format(",CFBundleVersion=%s", version));
    }
    info.append(',');
    info.append(getApplicationPath().getAbsolutePath());
    return info.toString();
  }

  /**
   * the content of the Info.plist for the app, as a json object.
   */
  public JSONObject getMetadata() {
    return metadata;
  }

  public List<String> getSupportedLanguagesCodes() {
    List<AppleLanguage> list = getSupportedLanguages();
    List<String> res = new ArrayList<>();
    for (AppleLanguage lang : list) {
      res.add(lang.getIsoCode());
    }
    return res;
  }

  /**
   * Get the list of languages the application is localized to.
   */
  List<AppleLanguage> getSupportedLanguages() {
    return languages;
  }

  public AppleLanguage getLanguage(String languageCode) {
    if (getSupportedLanguages().isEmpty()) {
      return AppleLanguage.emptyLocale(languageCode);
    }
    if (languageCode == null) {
      // default to english if none specified
      languageCode = "en";
    }
    for (AppleLanguage loc : getSupportedLanguages()) {
      if (languageCode.equals(loc.getIsoCode())) {
        return loc;
      }
    }
    throw new WebDriverException("Cannot find AppleLocale for " + languageCode);
  }

  public LanguageDictionary getDictionary(String languageCode) throws WebDriverException {
    return getDictionary(AppleLanguage.valueOf(languageCode));
  }

  public LanguageDictionary getDictionary(AppleLanguage language) throws WebDriverException {
    if (!language.exists()) {
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
  private ImmutableList<LanguageDictionary> loadDictionaries() {
    Map<String, LanguageDictionary> languageNameMap = new HashMap<>();
    for (File f : LanguageDictionary.getL10NFiles(app)) {
      String name = LanguageDictionary.extractLanguageName(f);
      LanguageDictionary res = languageNameMap.get(name);
      if (res == null) {
        res = new LanguageDictionary(name);
        languageNameMap.put(name, res);
      }
      try {
        // Load the content.
        JSONObject content = res.readContentFromBinaryFile(f);
        res.addJSONContent(content);
      } catch (Exception e) {
        throw new WebDriverException("Error loading content for l10n", e);
      }
    }
    List<LanguageDictionary> dicts = new ArrayList<>(languageNameMap.values());
    Collections.sort(dicts, new Comparator<LanguageDictionary>() {
      @Override
      public int compare(LanguageDictionary o1, LanguageDictionary o2) {
        return o1.getLanguage().compareTo(o2.getLanguage());
      }
    });
    return ImmutableList.copyOf(dicts);
  }

  private ImmutableList<AppleLanguage> loadLanguages() {
    ImmutableList.Builder<AppleLanguage> builder = ImmutableList.builder();
    for (LanguageDictionary dict : dictionaries) {
      builder.add(dict.getLanguage());
    }
    return builder.build();
  }

  public String translate(ContentResult res, AppleLanguage language) throws WebDriverException {
    LanguageDictionary destinationLanguage = getDictionary(language);
    return destinationLanguage.translate(res);
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
    Map<String, String> resourceByResourceName = new HashMap<>();
    String metadata =  getMetadata(ICON);
    if(metadata.equals("")){
      metadata = getFirstIconFile(BUNDLE_ICONS);
    }
    resourceByResourceName.put(ICON, metadata);
    return resourceByResourceName;
  }

  private String getFirstIconFile(String bundleIcons) {
    if (!metadata.has(bundleIcons)) {
        return "";
    }
    try {
        HashMap icons = (HashMap) metadata.get(bundleIcons);
        HashMap primaryIcon = (HashMap) icons.get("CFBundlePrimaryIcon");
        ArrayList iconFiles = (ArrayList) primaryIcon.get("CFBundleIconFiles");
        if (iconFiles != null) {
            return iconFiles.get(0).toString();
        } else {
            return "";
        }
    } catch (JSONException e) {
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
      List<Integer> res = new ArrayList<>();
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

  public static File findSafariLocation(File xcodeInstall, String sdkVersion) {
    IOSVersion version = new IOSVersion(sdkVersion);
    String v = version.getMajor()+"."+version.getMinor();
    File safariFolder = new File(xcodeInstall,
                        "/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator"
                        + v
                        + ".sdk/Applications/MobileSafari.app");
    if (!safariFolder.exists()) {
      log.warning("safari app doesn't exist: " + safariFolder.getAbsolutePath());
    }
    return safariFolder;
  }

  public static APPIOSApplication findSafariApp(File xcodeInstall, String sdkVersion) {
    File safariFolder = findSafariLocation(xcodeInstall, sdkVersion);
    if (!safariFolder.exists()) {
      throw new WebDriverException(safariFolder + " should be the safari app, but doesn't exist.");
    }
    return new APPIOSApplication(safariFolder.getAbsolutePath());
  }

  public void setDefaultDevice(DeviceType device, boolean putDefaultFirst) {
    try {
      File plist = new File(app, "Info.plist");

      PListFormat format = getFormat(plist);
      NSDictionary root = (NSDictionary) PropertyListParser.parse(new FileInputStream(plist));

      NSArray devices = (NSArray) root.objectForKey("UIDeviceFamily");
      int length = devices.getArray().length;
      if (length == 1) {
        return;
      }

      NSArray rearrangedArray = new NSArray(length);
      NSNumber defaultDevice = null;
      int index = putDefaultFirst? 1 : 0;
      for (int i = 0; i < length; i++) {
        NSNumber d = (NSNumber) devices.objectAtIndex(i);
        if (d.intValue() == device.getDeviceFamily()) {
          defaultDevice = d;
        } else {
          rearrangedArray.setValue(index, d);
          index++;
        }
      }
      if (defaultDevice == null) {
        throw new WebDriverException(
            "Cannot find device " + device + " in the supported device list.");
      }
      rearrangedArray.setValue(putDefaultFirst? 0 : index, defaultDevice);
      root.put("UIDeviceFamily", rearrangedArray);

      write(plist,root,format);
    } catch (Exception e) {
      throw new WebDriverException("Cannot change the default device for the app." + e.getMessage(), e);
    }
  }

  // TODO : fails for 64_bits
  // visible running NewSessionTests::supportApplicationWithMultipleDeviceFamily
  /**
   * Modifies the BuiltinFavorites....plist in safariCopies/safari.app to contain only "about:blank"
   */
  public void setSafariBuiltinFavorites() {
    File[] files = app.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.startsWith("BuiltinFavorites") && name.endsWith(".plist");
      }
    });
    for (File plist: files) {
      setSafariBuiltinFavories(plist);
    }
  }

  private void setSafariBuiltinFavories (File builtinFavoritesPList) {
    try {
      PListFormat format = getFormat(builtinFavoritesPList);

      NSArray root = new NSArray(1);
      NSDictionary favorite = new NSDictionary();
      favorite.put("Title", "about:blank");
      favorite.put("URL", "about:blank");
      root.setValue(0, favorite);

      write(builtinFavoritesPList, root, format);
    } catch (Exception e) {
      throw new WebDriverException("Cannot set " + builtinFavoritesPList.getAbsolutePath()
          + ": " + e.getMessage(), e);
    }
  }

  enum PListFormat{
    binary, text, xml
  }

  private void write(File dest, NSObject content, PListFormat format) throws IOException {
    switch (format) {
    case binary:
      BinaryPropertyListWriter.write(dest, content);
      break;
    case xml:
      PropertyListParser.saveAsXML(content, dest);
      break;
    case text:
      if (content instanceof NSDictionary)
        PropertyListParser.saveAsASCII((NSDictionary) content, dest);
      else if (content instanceof NSArray)
        PropertyListParser.saveAsASCII((NSArray) content, dest);
      else
        throw new IllegalArgumentException("Invalid content type for ascii: " + content.getClass());
      break;
    default:
      throw new IllegalArgumentException("Invalid plist output format: " + format);
    }
  }

  private PListFormat getFormat(File f) throws IOException {
    FileInputStream fis = new FileInputStream(f);
    byte b[] = new byte[8];
    fis.read(b,0,8);
    String magicString = new String(b);
    fis.close();
    if (magicString.startsWith("bplist")) {
      return PListFormat.binary;
    } else if (magicString.trim().startsWith("(") || magicString.trim().startsWith("{")
        || magicString.trim().startsWith("/")) {
      return PListFormat.text;
    } else {
      return PListFormat.xml;
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
    if (desiredCapabilities.getCapability(IOSCapabilities.SIMULATOR) != null &&
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
    if (!appCapability.getSupportedDevices().contains(desiredCapabilities.getDevice())) {
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
          + " Localization related features won't be available.");
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
    List<DeviceType> families = new ArrayList<>();
    String s = getMetadata(IOSCapabilities.DEVICE_FAMILLY);
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

  public IOSRunningApplication createInstance(AppleLanguage language) {
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