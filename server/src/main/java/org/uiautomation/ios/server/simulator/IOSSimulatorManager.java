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
package org.uiautomation.ios.server.simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.command.impl.NewSession;
import org.uiautomation.ios.server.instruments.ClassicCommands;
import org.uiautomation.ios.server.instruments.Command;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;


// java version ( simplified )
// of http://code.google.com/p/ios-sim-locale/source/browse/trunk/ios-sim-locale.m
/**
 * setting the plist file to the correct local. Tested on mac 10.7. May work on other version. The
 * asusmption made is that the plist file read by the ios simulator is of binary1 format. See the
 * mac command line plutils for the formats.
 * 
 * 
 */
public class IOSSimulatorManager implements IOSDeviceManager {

  private final List<String> sdks;
  private static final String TEMPLATE = "/globalPlist.json";
  private static final String PLUTIL = "/usr/bin/plutil";

  private static final String oldSDKPath =
      "/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator%s.sdk";
  private static final String newSDKPath =
      "/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator%s.sdk";

  private final File sdk;
  private final String desiredSDKVersion;
  private File contentAndSettingsFolder;
  // simulator plist, to choose language and locale
  private File globalPreferencePlist;
  public static final String SIMULATOR_PROCESS_NAME = "iPhone Simulator";


  /**
   * manages a single instance of the instruments process. Only 1 process can run at a given time.
   * 
   * @param desiredSDKVersion the SDK version. For instance 5.0 or 4.3
   * @param device
   * @throws IOSAutomationSetupException
   */
  public IOSSimulatorManager(String desiredSDKVersion, IOSDevice device)
      throws IOSAutomationSetupException {
    if (isSimulatorRunning()) {
      throw new IOSAutomationSetupException("another instance of the simulator is already running.");
    }

    this.sdks = ClassicCommands.getInstalledSDKs();
    this.desiredSDKVersion = validateSDK(desiredSDKVersion);
    this.sdk = findSDK(desiredSDKVersion);

    //setDefaultSimulatorPreference("currentSDKRoot", sdk.getAbsolutePath());
    //setDefaultSimulatorPreference("SimulateDevice", device.getName());

    this.contentAndSettingsFolder = getContentAndSettingsFolder();
    this.globalPreferencePlist = getGlobalPreferenceFile();
  }



  /**
   * update the preference of the simulator. Similar to using the IOS Simulator menu > Hardware >
   * [Device | Version ]
   * 
   * @param key
   * @param value
   * @throws IOSAutomationSetupException
   */
  private void setDefaultSimulatorPreference(String key, String value)
      throws IOSAutomationSetupException {
    List<String> com = new ArrayList<String>();
    com.add("defaults");
    com.add("write");
    com.add("com.apple.iphonesimulator");
    com.add(key);
    com.add(value);

    Command updatePreference = new Command(com, true);
    updatePreference.executeAndWait();
  }

  private File findSDK(String version) throws IOSAutomationSetupException {
    File old = new File(String.format(oldSDKPath, version));
    if (old.exists()) {
      return old;
    }
    File newPath = new File(String.format(newSDKPath, version));
    if (newPath.exists()) {
      return newPath;
    }
    throw new IOSAutomationSetupException("Cannot find iphone simulator SDK for version " + version);

  }

  private String validateSDK(String sdk) throws IOSAutomationSetupException {
    if (!sdks.contains(sdk)) {
      throw new IOSAutomationSetupException("desired sdk " + sdk + " isn't installed. Installed :"
          + sdks);
    }
    return sdk;
  }


  private boolean isSimulatorRunning() throws IOSAutomationSetupException {
    return ClassicCommands.isRunning(SIMULATOR_PROCESS_NAME);
  }



  /**
   * set the emulator to the given locale.Required a clean context (can only be done after "reset
   * content and settings" )
   * 
   * @param locale fr_FR
   * @param language fr
   * @throws IOSAutomationSetupException
   */
  public void setL10N(String locale, String language) throws IOSAutomationSetupException {
    try {
      JSONObject plistJSON = getPreferenceFile(locale, language);
      writeOnDisk(plistJSON);
    } catch (Exception e) {
      throw new IOSAutomationSetupException("cannot configure simulator", e);
    }

  }

  /**
   * Does what IOS Simulator - Reset content and settings menu does, by deleting the files on disk.
   * The simulator shouldn't be running when that is done.
   */
  public void resetContentAndSettings() {
    if (hasContentAndSettingsFolder()) {
      boolean ok = deleteRecursive(getContentAndSettingsFolder());
      if (!ok) {
        System.err.println("cannot delete content and settings folder " + contentAndSettingsFolder);
      }
    }
    boolean ok = contentAndSettingsFolder.mkdir();
    if (!ok) {
      System.err.println("couldn't re-create" + contentAndSettingsFolder);
    }
  }

  /**
   * stopping the simulator at the end of the test.
   * 
   * @throws IOSAutomationSetupException
   */
  public void cleanupDevice() throws IOSAutomationSetupException {
    ClassicCommands.killall(SIMULATOR_PROCESS_NAME);
  }



  private File createTmpFile(JSONObject content) throws IOException, JSONException {
    File res = File.createTempFile("global", ".json");
    BufferedWriter out = new BufferedWriter(new FileWriter(res));
    out.write(content.toString(2));
    out.close();
    return res;

  }

  private void writeOnDisk(JSONObject plistJSON) throws IOException, JSONException {
    if (globalPreferencePlist.exists()) {
      // to be on the safe side. If the emulator already runs, it won't work anyway.
      throw new IOSAutomationException(globalPreferencePlist + "already exists.Cannot create it.");
    }
    // make sure the folder is ready for the plist file
    globalPreferencePlist.getParentFile().mkdirs();

    checkPlUtil();

    File from = createTmpFile(plistJSON);

    List<String> command = new ArrayList<String>();
    command.add(PLUTIL);
    command.add("-convert");
    command.add("binary1");
    command.add("-o");
    command.add(globalPreferencePlist.getAbsolutePath());
    command.add(from.getAbsolutePath());

    ProcessBuilder b = new ProcessBuilder(command);
    try {
      Process p = b.start();
      int i = p.waitFor();
      if (i != 0) {
        throw new IOSAutomationException("convertion to binary pfile failed.exitCode=" + i);
      }
    } catch (Exception e) {
      throw new IOSAutomationException("failed to run " + command.toString(), e);
    }

  }



  private void checkPlUtil() {
    File f = new File(PLUTIL);
    if (!f.exists() || !f.canExecute()) {
      throw new IOSAutomationException("Cannot access " + PLUTIL);
    }

  }


  private File getContentAndSettingsFolder() {
    String home = System.getProperty("user.home");
    String s =
        String
            .format("%s/Library/Application Support/iPhone Simulator/%s", home, desiredSDKVersion);
    File f = new File(s);
    if (!f.exists()) {
      throw new IOSAutomationException(f + " doesn't exist.Is the sdk version correct ?");
    }
    return f;
  }

  private File getGlobalPreferenceFile() {
    File folder = new File(contentAndSettingsFolder + "/Library/Preferences/");
    File global = new File(folder, ".GlobalPreferences.plist");
    System.out.println(global);
    return global;
  }

  private JSONObject getPreferenceFile(String locale, String language) throws JSONException,
      IOException {
    JSONObject res = loadGlobalPreferencesTemplate();
    JSONArray languages = new JSONArray();
    languages.put(language);
    res.put("AppleLanguages", languages);
    res.put("AppleLocale", locale);
    return res;

  }

  private JSONObject loadGlobalPreferencesTemplate() throws JSONException, IOException {
    InputStream is = NewSession.class.getResourceAsStream(TEMPLATE);
    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, "UTF-8");
    String content = writer.toString();
    JSONObject config = new JSONObject(content);
    return config;
  }


  private boolean deleteRecursive(File folder) {
    if (folder.isDirectory()) {
      String[] children = folder.list();
      for (int i = 0; i < children.length; i++) {
        File delMe = new File(folder, children[i]);
        boolean success = deleteRecursive(delMe);
        if (!success) {
          System.err.println("cannot delete " + delMe);
        }
      }
    }
    return folder.delete();
  }



  private boolean hasContentAndSettingsFolder() {
    File f = getContentAndSettingsFolder();
    return f.exists();
  }



}
