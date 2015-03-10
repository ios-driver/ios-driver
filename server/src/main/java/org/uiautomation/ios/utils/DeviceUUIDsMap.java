package org.uiautomation.ios.utils;

import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceUUIDsMap implements CommandOutputListener {

  private static final Pattern iOSPattern = Pattern.compile("-- iOS (.*) --");

  private static final Pattern simulatorPattern = Pattern
      .compile("    (.*) \\((\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12})\\) \\((\\w*\\s?\\w*)\\)");

  private static final Pattern iOSToSdkPattern = Pattern
      .compile("iOS ([\\d\\.]+) \\(([\\d\\.]+) - (\\S+)\\) \\(com.apple.CoreSimulator.SimRuntime.iOS-\\d-\\d\\)");

  private Map<String, String> simulatorToUUID = new HashMap<>();

  private Map<String, String> simulatorState = new HashMap<>();

  private Map<String, SdkVersionBuildInfo> iOSToSdkMap = new HashMap<String, SdkVersionBuildInfo>();

  private WebDriverException commandParsingException = null;

  private String iOSUnderScan = null;

  private String commandOutput = null;

  public void loadData() {
    List<String> listArgs = new ArrayList<>();
    listArgs.add("xcrun");
    listArgs.add("simctl");
    listArgs.add("list");
    Command listCommand = new Command(listArgs, false);
    listCommand.registerListener(this);
    listCommand.executeAndWait();
    if (isParsingExceptionEncountered()) {
      throw commandParsingException;
    }
  }

  @Override
  public void stdout(String log) {
    if (isParsingExceptionEncountered()) {
      return;
    }
    commandOutput = log;
    parseCommandOutput();
  }

  private boolean isParsingExceptionEncountered() {
    return commandParsingException != null;
  }

  private void parseCommandOutput() {
    try {
      checkForIOSToSDKMatch();
      setUpIOSUnderScan();
      checkForDeviceToUUIDMatch();
    } catch (WebDriverException webDriverException) {
      commandParsingException = webDriverException;
    }
  }

  private void checkForIOSToSDKMatch() {
    Matcher iOSToSdkMatcher = iOSToSdkPattern.matcher(commandOutput);
    if (iOSToSdkMatcher.matches()) {
      if (iOSToSdkMatcher.group(1) == null || iOSToSdkMatcher.group(2) == null) {
        throw new WebDriverException("Cannot find iOS/SDK version in the command output: "
            + iOSToSdkMatcher.group(0));
      }
      iOSUnderScan = iOSToSdkMatcher.group(1);
      addToSDKMap(iOSToSdkMatcher.group(2), iOSToSdkMatcher.group(3));
      iOSUnderScan = null;
    }
  }

  private void addToSDKMap(String sdkVersion, String build) {
    SdkVersionBuildInfo versionBuildInfo = new SdkVersionBuildInfo(sdkVersion, build);
    iOSToSdkMap.put(iOSUnderScan, versionBuildInfo);
  }

  private void setUpIOSUnderScan() {
    Matcher sdkMatcher = iOSPattern.matcher(commandOutput);
    if (sdkMatcher.matches()) {
      iOSUnderScan = sdkMatcher.group(1);
    }
  }

  private void checkForDeviceToUUIDMatch() {
    Matcher simulatorMatcher = simulatorPattern.matcher(commandOutput);
    if (simulatorMatcher.matches()) {
      String device = simulatorMatcher.group(1);
      String uuid = simulatorMatcher.group(2);
      String state = simulatorMatcher.group(3);
      String SimulatorName = iOSToSdkMap.get(iOSUnderScan).getSdkVersion() + "," + device;
      simulatorToUUID.put(SimulatorName, uuid);
      simulatorState.put(SimulatorName, state);
    }
  }

  @Override
  public void stderr(String log) {
  }

  public String getUUID(String sdkVersion, String deviceName) {
    return simulatorToUUID.get(sdkVersion + "," + deviceName);
  }

  public String getState(String sdkVersion, String deviceName) {
    return simulatorState.get(sdkVersion + "," + deviceName);
  }

  /**
   * DataStructure to hold SDK version and Build number for a particular iOS Version
   */
  private static class SdkVersionBuildInfo {

    private final String sdkVersion;

    private final String build;

    SdkVersionBuildInfo(String sdkVersion, String build) {
      this.sdkVersion = sdkVersion;
      this.build = build;
    }

    public String getSdkVersion() {
      return sdkVersion;
    }

    public String getBuild() {
      return build;
    }

    @Override
    public String toString() {
      return "[SDK Version: " + sdkVersion + ", Build: " + build + "]";
    }
  }
}
