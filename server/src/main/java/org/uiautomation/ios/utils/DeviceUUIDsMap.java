package org.uiautomation.ios.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceUUIDsMap implements CommandOutputListener {

  private static final Pattern sdkPattern = Pattern.compile("-- iOS (.*) --");
  private static final Pattern simulatorPattern =
    Pattern.compile("    (.*) \\((\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12})\\) \\(\\w*\\)");

  private String currentSDK;
  private Map<String, String> simulatorToUUID = new HashMap<>();

  public void loadData() {
    List<String> listArgs = new ArrayList<>();
    listArgs.add("xcrun");
    listArgs.add("simctl");
    listArgs.add("list");
    Command listCommand = new Command(listArgs, false);
    listCommand.registerListener(this);
    listCommand.executeAndWait();
  }

  @Override
  public void stdout(String log) {
    Matcher sdkMatcher = sdkPattern.matcher(log);
    if (sdkMatcher.matches()) {
      currentSDK = sdkMatcher.group(1);
    }

    Matcher simulatorMatcher = simulatorPattern.matcher(log);
    if (simulatorMatcher.matches()) {
      String device = simulatorMatcher.group(1);
      String uuid = simulatorMatcher.group(2);

      simulatorToUUID.put(currentSDK + "," + device, uuid);
    }
  }

  @Override
  public void stderr(String log) {
  }

  public String getUUID(String sdkVersion, String deviceName) {
    return simulatorToUUID.get(sdkVersion + "," + deviceName);
  }
}
