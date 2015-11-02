package org.uiautomation.ios.grid;

import java.util.Map;

import org.openqa.grid.internal.utils.DefaultCapabilityMatcher;
import org.uiautomation.ios.IOSServerManager;

/**
 * <code>IOSGenericCapabilityMatcher</code> extends {@link DefaultCapabilityMatcher} and can be used in Selenium Grid
 * environment where iOS driver needs to registered as a regular node to the Selenium Grid along with other node types.
 * The call to match a capability is delegated to {@link DefaultCapabilityMatcher} if iOS server is not able to find a
 * match for the requested capability.
 */
public class IOSGenericCapabilityMatcher extends DefaultCapabilityMatcher {

  private static final String BUNDLE_IDENTIFIER = "CFBundleIdentifier";

  private static final String BUNDLE_NAME = "CFBundleName";

  @Override
  public boolean matches(Map<String, Object> currentCapability, Map<String, Object> requestedCapability) {

    // Check the validity of capability and for a possible match from IOSServerManager, if all these
    // calls return false delegate to DefaultCapabilityMatcher of Selenium server to find a match
    // in Grid environment.
    return (isValid(currentCapability) 
        && isValid(requestedCapability) 
        && IOSServerManager.matches(currentCapability, requestedCapability)) 
        || super.matches(currentCapability, requestedCapability);
}

  /*
   * Checks the validity of a capability by checking for not-null reference and
   * the availability of CFBundleIdentifier and CFBundleName keys.
   */
  private boolean isValid(Map<String, Object> capability) {
    boolean validCapability = false;
    validCapability = capability != null && capability.containsKey(BUNDLE_IDENTIFIER)
        && capability.containsKey(BUNDLE_NAME);
    return validCapability;
  }
}
