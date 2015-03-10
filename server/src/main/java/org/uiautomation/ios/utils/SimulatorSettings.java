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
package org.uiautomation.ios.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.HostInfo;
import org.uiautomation.ios.command.uiautomation.NewSessionNHandler;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.instruments.InstrumentsVersion;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimulatorSettings {


  private static final Logger log = Logger.getLogger(SimulatorSettings.class.getName());
  private static final String PLUTIL = "/usr/bin/plutil";
  private static final String TEMPLATE = "/globalPlist.json";
  private static final
  String
      SDK_LOCATION =
      "Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/";

  private static final int NUMBER_TRIES_GETTING_UUID = 3;
  private static final int SLEEP_TIME_BETWEEN_TRIES = 2000;

  private final String exactSdkVersion;
  private final boolean is64bit;
  private final File contentAndSettingsFolder;
  private final File globalPreferencePlist;
  private final HostInfo info;
  private String deviceUUID;
  private String deviceName;

  private InstrumentsVersion instrumentsVersion;

  public SimulatorSettings(HostInfo info, String sdkVersion, boolean is64bit, DeviceType device,
                           DeviceVariation variation) throws NotActiveException {
    this.exactSdkVersion = sdkVersion;
    this.is64bit = is64bit;
    this.info = info;
    this.instrumentsVersion = info.getInstrumentsVersion();
    setVariation(device, variation, sdkVersion);
    if (instrumentsVersion.getMajor() >= 6) {
      deviceUUID = determineDeviceUUID();
    }
    this.contentAndSettingsFolder = getContentAndSettingsFolder();
    this.globalPreferencePlist = getGlobalPreferenceFile();
  }

  public void setLocationPreference(boolean authorized, String bundleId) {
    File f = new File(contentAndSettingsFolder + "/Library/Caches/locationd/", "clients.plist");

    try {
      JSONObject clients = new JSONObject();
      JSONObject options = new JSONObject();
      options.put("Whitelisted", false);
      options.put("BundleId", bundleId);
      options.put("Authorized", authorized);
      clients.put(bundleId, options);
      writeOnDisk(clients, f);
    } catch (Exception e) {
      throw new WebDriverException("cannot set location in " + f.getAbsolutePath(), e);
    }
  }

  /**
   * the default keyboard options aren't good for automation. For instance it automatically
   * capitalize the first letter of sentences etc. Getting rid of all that to have the keyboard
   * execute requests without changing them.
   */
  public void setKeyboardOptions() {
    File folder = new File(contentAndSettingsFolder + "/Library/Preferences/");
    File preferenceFile = new File(folder, "com.apple.Preferences.plist");

    try {
      JSONObject preferences = new JSONObject();
      preferences.put("KeyboardAutocapitalization", false);
      preferences.put("KeyboardAutocorrection", false);
      preferences.put("KeyboardCapsLock", false);
      preferences.put("KeyboardCheckSpelling", false);
      writeOnDisk(preferences, preferenceFile);
    } catch (Exception e) {
      throw new WebDriverException("cannot set options in " + preferenceFile.getAbsolutePath(), e);
    }
  }

  /**
   * Set the Accessibility preferences. Overrides the values in 'com.apple.Accessibility.plist' file.
   */
  public void setAccessibilityOptions() {
    
    // Not enabling ApplicationAccessibility may cause intruments to fail with the error 
    // ScriptAgent[1170:2f07] Failed to enable accessiblity, kAXErrorServerNotFound
    // The above error is more prominent in Xcode5.1.1 when tested under OSX 10.9.5
    // Setting ApplicationAccessibilityEnabled for Xcode6.0
    File folder = new File(contentAndSettingsFolder + "/Library/Preferences/");
    File preferenceFile = new File(folder, "com.apple.Accessibility.plist");
    try {
      JSONObject preferences = new JSONObject();
      if (instrumentsVersion.getMajor() < 6) {
        
        // ex: <key>ApplicationAccessibilityEnabled</key><true/>
        preferences.put("ApplicationAccessibilityEnabled", true);
      } else {
        
        // Xcode6.0 has integer datatype for ApplicationAccessibilityEnabled
        // ex: <key>ApplicationAccessibilityEnabled</key><integer>1</integer>
        preferences.put("ApplicationAccessibilityEnabled", 1);
      }
      writeOnDisk(preferences, preferenceFile);
    } catch (Exception e) {
      throw new WebDriverException("cannot set options in " + preferenceFile.getAbsolutePath(), e);
    }
  }

  public void setMobileSafariOptions() {
    File preferenceFile = getMobileSafariPreferencesFile();
    try {
      JSONObject preferences = new JSONObject();
      preferences.put("WarnAboutFraudulentWebsites", false);
      writeOnDisk(preferences, preferenceFile);
    } catch (Exception e) {
      throw new WebDriverException("cannot set options in " + preferenceFile.getAbsolutePath(), e);
    }
  }

  private File getMobileSafariPreferencesFile() {
    File folder = new File(contentAndSettingsFolder + "/Library/Preferences/");
    return new File(folder, "com.apple.mobilesafari.plist");
  }

  /**
   * set the emulator to the given locale.Required a clean context (can only be done after "reset
   * content and settings" )
   *
   * @param locale   fr_FR
   * @param language fr
   */
  public void setL10N(String locale, String language) {
    try {
      JSONObject plistJSON = getPreferenceFile(locale, language);
      writeOnDisk(plistJSON, globalPreferencePlist);
    } catch (Exception e) {
      throw new WebDriverException("cannot configure simulator", e);
    }
  }

  /**
   * update the preference to have the simulator start in the correct mode ( ie retina vs normal,
   * iphone screen size ).
   */
  public void setVariation(DeviceType device, DeviceVariation variation, String desiredSDKVersion)
      throws WebDriverException {
    deviceName = AppleMagicString.getSimulateDeviceValue(device, variation, desiredSDKVersion, instrumentsVersion);
    setDefaultSimulatorPreference("SimulateDevice", deviceName);

    String subfolder = getSDKSubfolder(desiredSDKVersion);
    File sdk = new File(info.getXCodeInstall(), subfolder);

    if (!sdk.exists()) {
      throw new WebDriverException(
          "Cannot point simulator to requested sdk version " + sdk.getAbsolutePath());
    }
    setDefaultSimulatorPreference("currentSDKRoot", sdk.getAbsolutePath());
  }

  private String getSDKSubfolder(String desiredSDKVersion) {
    String suffix = desiredSDKVersion;
    if("7.0.3".equals(desiredSDKVersion)){
      suffix = "7.0";
    }
    return SDK_LOCATION + "iPhoneSimulator" + suffix + ".sdk";
  }

  private String determineDeviceUUID() throws NotActiveException {
    DeviceUUIDsMap uuidsMap = new DeviceUUIDsMap();
    uuidsMap.loadData();
    String uuid = uuidsMap.getUUID(exactSdkVersion, deviceName);
    if (uuid == null) {
      throw new NotFoundException("Couldn't find UUID for device " + deviceName + " with SDK " + exactSdkVersion);
    }
    int countingTries = 0;
    String state = uuidsMap.getState(exactSdkVersion, deviceName);
    while (countingTries < NUMBER_TRIES_GETTING_UUID && state.equals("Shutting Down")) {
      System.err.println("Device is shutting down. Wait 2 seconds.");
      try {
        Thread.sleep(SLEEP_TIME_BETWEEN_TRIES);
      } catch (InterruptedException e) {
      }
      uuidsMap.loadData();
      state = uuidsMap.getState(exactSdkVersion, deviceName);
      ++countingTries;
    }
    if (state.equals("Shutting down"))
      throw new NotActiveException("UUID for device " + deviceName + " with SDK " + exactSdkVersion
              + " is not " + "available to use. " + "Current state:" + state);
    return uuid;
  }

  public InstrumentsVersion getInstrumentsVersion() {
    return instrumentsVersion;
  }

  public void setSimulatorScale(String scale) {
    if (scale != null) {
      // error check scale value
      float fScale = Float.parseFloat(scale);
      if (fScale <= 0) {
        throw new WebDriverException("invalid simulator scale: " + scale);
      }
      setDefaultSimulatorPreference("SimulatorWindowLastScale", scale);
    }
  }

  /**
   * update the preference of the simulator. Similar to using the IOS Simulator menu > Hardware >
   * [DeviceType | Version ]
   */
  private void setDefaultSimulatorPreference(String key, String value) {
    List<String> com = new ArrayList<>();
    com.add("defaults");
    com.add("write");
    com.add("com.apple.iphonesimulator");
    com.add(key);
    com.add(String.format("\"%s\"", value));

    Command updatePreference = new Command(com, true);
    updatePreference.executeAndWait();
  }

  /**
   * Does what IOS Simulator - Reset content and settings menu does, by deleting the files on disk.
   * The simulator shouldn't be running when that is done.
   */
  public void resetContentAndSettings() {
    if (instrumentsVersion.getMajor() < 6) {
      if (hasContentAndSettingsFolder()) {
        boolean ok = deleteRecursive(getContentAndSettingsFolder());
        if (!ok) {
          System.err.println("cannot delete content and settings folder " + contentAndSettingsFolder);
        }
      }

      // Wipe the system.log.
      String deviceLogDir = System.getProperty("user.home") +
          "/Library/Logs/iOS Simulator/" + exactSdkVersion + ((is64bit) ? "-64" : "");
      File deviceLog = new File(deviceLogDir, "system.log");
      if (deviceLog.exists()) {
        deviceLog.delete();
      }

      boolean ok = contentAndSettingsFolder.mkdirs();
      if (!ok) {
        System.err.println("couldn't re-create: " + contentAndSettingsFolder);
      }
    } else {
      // Starting with Xcode 6 and later, we can use simctl to do the hard work for us.
      // If it fails, we have to turn the device off first.
      if (!eraseSimulator()) {
        log.log(Level.WARNING, "Reset content and settings failed, " +
            "possibly device is in booted state, shutdown device = " + deviceUUID);
        List<String> simctlArgs = new ArrayList<>();
        simctlArgs = Arrays.asList("xcrun", "simctl", "shutdown", deviceUUID);
        Command simctlCmd = new Command(simctlArgs, true);

        // Run command 'xcrun simctl shutdown <uuid>'
        simctlCmd.executeAndWait(false);

        // Retry
        eraseSimulator();
      }
    }
  }

  private boolean eraseSimulator() {
    assert instrumentsVersion.getMajor() >= 6;

    // Starting with Xcode 6 and later, we can use simctl to do the hard work for us.
    List<String> simctlArgs = new ArrayList<>();
    simctlArgs.add("xcrun");
    simctlArgs.add("simctl");
    simctlArgs.add("erase");
    simctlArgs.add(deviceUUID);
    Command simctlCmd = new Command(simctlArgs, true);

    // if the device is still in booted state erase returns with error code 146
    int exitCode = simctlCmd.executeAndWait(true);
    if (exitCode == 146) {
      return false;
    } else if (exitCode != 0) {
      throw new WebDriverException("execution failed. Exit code =" + exitCode + " , command was: "
        + simctlCmd.commandString());
    }

    // Wipe the system.log, since simctl doesn't do it.
    String deviceLogDir = System.getProperty("user.home") +
      "/Library/Logs/CoreSimulator/" + deviceUUID;
    File deviceLog = new File(deviceLogDir, "system.log");
    if (deviceLog.exists()) {
      deviceLog.delete();
    }

    return true;
  }

  public void writeContentFile(String path, byte[] fileContents) {
      if (instrumentsVersion.getMajor() >= 6 && path.startsWith("Media/DCIM/100APPLE")) {
          // this is a photo, we're on Xcode 6, let simctl push it into place
          try {
              // the simulator needs to be booted first
              List<String> bootInvocation = Arrays.asList("xcrun", "simctl", "boot", deviceUUID);
              Command bootCommand = new Command(bootInvocation, true);
              int simCtlResult = bootCommand.executeAndWait(false);
              if (simCtlResult != 0 && simCtlResult != 146) {
                  throw new WebDriverException("image copy failed. Couldn't boot simulator, exit code = " + simCtlResult);
              }
              File photo = File.createTempFile("photo", ".jpg");
              FileOutputStream photoWriter = new FileOutputStream(photo);
              photoWriter.write(fileContents);
              photoWriter.close();
              // usage: xcrun simctl addphoto <uuid> <path>
              List<String> addPhotoInvocation = Arrays.asList("xcrun", "simctl", "addphoto", deviceUUID, photo.getAbsolutePath());
              Command addPhotoCommand = new Command(addPhotoInvocation, true);
              simCtlResult = addPhotoCommand.executeAndWait(false);
              photo.delete();
              if (simCtlResult != 0) {
                  throw new WebDriverException("image copy failed. Exit code =" + simCtlResult +", command was: "
                  + addPhotoCommand.toString());
              }
              //now shut the sim back down, or Bad Things Happen™
              List<String> shutdownInvocation = Arrays.asList("xcrun", "simctl", "shutdown", deviceUUID);
              Command shutdownCommand = new Command(shutdownInvocation, true);
              simCtlResult = shutdownCommand.executeAndWait(false);
              if (simCtlResult != 0) {
                  System.err.println("simctl didn't shut down cleanly. Exit code =" + simCtlResult);
              }
          } catch (IOException e) {
              throw new WebDriverException("image copy failed. Could not write image " + path);
          }
      } else {
          File newFile = new File(getContentAndSettingsFolder(), path);
          newFile.getParentFile().mkdirs();
          try {
              newFile.createNewFile();
              FileOutputStream out = new FileOutputStream(newFile);
              out.write(fileContents);
          } catch (IOException e) {
              System.err.println("could not create " + path);
          }
      }
  }

  private File getContentAndSettingsParentFolder() {
    String home = System.getProperty("user.home");
    String subdir = (instrumentsVersion.getMajor() < 6) ?
      "Library/Application Support/iPhone Simulator" :
      "Library/Developer/CoreSimulator/Devices/";

    return new File(home, subdir);
  }

  private File getContentAndSettingsFolder() {
    String deviceDir;
    if (instrumentsVersion.getMajor() < 6) {
      String folder = exactSdkVersion;
      if (is64bit) {
        folder += "-64";
      }
      deviceDir = folder;
    } else {
      deviceDir = deviceUUID + "/data";
    }
    return new File(getContentAndSettingsParentFolder(), deviceDir);
  }

  private boolean deleteRecursive(File folder) {
    if (folder.isDirectory()) {
      if (isSymLink(folder)) {
        return folder.delete();
      }
      for (String child : folder.list()) {
        File delMe = new File(folder, child);
        boolean success = deleteRecursive(delMe);
        if (!success) {
          log.warning("cannot delete " + delMe
                      + ".Are you trying to start a test while a simulator is still running ?");
        }
      }
    }
    return folder.delete();
  }

  private boolean isSymLink(File folder) {
    return Files.isSymbolicLink(Paths.get(folder.toURI()));
  }

  private boolean hasContentAndSettingsFolder() {
    return getContentAndSettingsFolder().exists();
  }



  private JSONObject loadGlobalPreferencesTemplate() throws JSONException, IOException {
    InputStream is = NewSessionNHandler.class.getResourceAsStream(TEMPLATE);
    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, "UTF-8");
    String content = writer.toString();
    return new JSONObject(content);
  }

  private JSONObject getPreferenceFile(String locale, String language)
      throws JSONException, IOException {
    JSONObject res = loadGlobalPreferencesTemplate();
    JSONArray languages = new JSONArray();
    languages.put(language);
    res.put("AppleLanguages", languages);
    res.put("AppleLocale", locale);
    return res;
  }

  private File getGlobalPreferenceFile() {
    File folder = new File(contentAndSettingsFolder + "/Library/Preferences/");
    return new File(folder, ".GlobalPreferences.plist");
  }

  // TODO use plist utils.
  private void writeOnDisk(JSONObject plistJSON, File destination)
      throws IOException, JSONException {
    if (destination.exists()) {
      // This is possible if we start with capability "reuseContentAndSettings"
      log.info(destination + " already exists. Overwriting data");
    }

    // make sure the folder is ready for the plist file
    destination.getParentFile().mkdirs();

    checkPlUtil();

    File from = createTmpFile(plistJSON);

    List<String> command = new ArrayList<>();
    command.add(PLUTIL);
    command.add("-convert");
    command.add("binary1");
    command.add("-o");
    command.add(destination.getAbsolutePath());
    command.add(from.getAbsolutePath());

    ProcessBuilder b = new ProcessBuilder(command);
    int i;
    try {
      Process p = b.start();
      i = p.waitFor();
    } catch (Exception e) {
      throw new WebDriverException("failed to run " + command.toString(), e);
    }
    if (i != 0) {
      throw new WebDriverException("conversion to binary plist failed. exitCode=" + i);
    }
  }

  private File createTmpFile(JSONObject content) throws IOException, JSONException {
    File res = File.createTempFile("global", ".json");
    BufferedWriter out = new BufferedWriter(new FileWriter(res));
    out.write(content.toString(2));
    out.close();
    return res;
  }

  private void checkPlUtil() {
    File f = new File(PLUTIL);
    if (!f.exists() || !f.canExecute()) {
      throw new WebDriverException("Cannot access " + PLUTIL);
    }
  }

  public void installTrustStore(String trustStore) {
    if (trustStore == null) {
      return;
    }

    // executes:
    // mkdir ~/"Library/Application Support/iPhone Simulator/7.0/Library/Keychains"
    // cp libs/ios/TrustStore.sqlite3 ~/"Library/Application Support/iPhone Simulator/7.0/Library/Keychains"
    File keychainsDir = new File(contentAndSettingsFolder + "/Library/Keychains");
    log.info("installing -trustStore: " + trustStore + " in " + keychainsDir.getAbsolutePath());
    File sourceFile = new File(trustStore);
    if (!sourceFile.exists()) {
      log.severe(
          "-trustStore: source trust store file doesn't exist: " + sourceFile.getAbsolutePath());
      return;
    }
    File destFile = new File(keychainsDir, "TrustStore.sqlite3");
    try {
      if (!keychainsDir.exists()) {
        if (!keychainsDir.mkdir()) {
          log.severe(
              "-trustStore: could not create Keychains dir: " + keychainsDir.getAbsolutePath());
          return;
        }
      }
      FileUtils.copyFile(sourceFile, destFile, false);
    } catch (Exception e) {
      log.severe("cannot install trust store file " + sourceFile.getAbsolutePath()
                 + " to " + destFile.getAbsolutePath() + ": " + e);
    }
  }
}
