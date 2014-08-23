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

package org.uiautomation.ios.instruments;

import org.apache.commons.codec.binary.Base64;
import org.libimobiledevice.ios.driver.binding.exceptions.SDKException;
import org.libimobiledevice.ios.driver.binding.services.DebugService;
import org.libimobiledevice.ios.driver.binding.services.DeviceService;
import org.libimobiledevice.ios.driver.binding.services.IOSDevice;
import org.libimobiledevice.ios.driver.binding.services.ScreenshotService;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.RealDevice;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.command.UIAScriptRequest;
import org.uiautomation.ios.instruments.commandExecutor.UIAutomationCommandExecutor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi;

/**
 * An implementation of {@link Instruments} that does not use instruments.
 */
public class NoInstrumentsImplementationAvailable implements Instruments {

  private final ServerSideSession session;
  private final ScreenshotSDK screenshotService;
  private final IOSDevice device;
  private final DebugService service;

  /**
   * this implementation is only relevant for real device safari, when instruments cannot be used.
   */
  @Override
  public boolean isCompatible(ServerSideSession session) {
    return session.isSafariRealDevice();
  }

  public NoInstrumentsImplementationAvailable(ServerSideSession session)
      throws SDKException {
    this.session = session;
    String uuid = ((RealDevice) session.getDevice()).getUuid();

    device = DeviceService.get(uuid);
    screenshotService = new ScreenshotSDK(device);
    service = new DebugService(device);

  }

  @Override
  public void start(long timeOut) throws InstrumentsFailedToStartException {

    try {
      service.startSafari();
    } catch (IOException | SDKException e) {
      throw new WebDriverException("Cannot start safari : " + e.getMessage(), e);
    }

    Response r = session.getCachedCapabilityResponse();
    if (r == null) {
      r = new Response();
      r.setSessionId(session.getSessionId());
      Map<String, Object> o = new HashMap<>();
      List<String> ls = session.getApplication().getSupportedLanguagesCodes();

      o.put("supportedLocales", ls);
      o.put("takesScreenshot", true);
      o.put(IOSCapabilities.CONFIGURABLE, true);
      o.put(IOSCapabilities.ELEMENT_TREE, true);
      o.put(IOSCapabilities.IOS_SEARCH_CONTEXT, true);
      o.put(IOSCapabilities.IOS_TOUCH_SCREEN, true);

      o.put("rotatable", true);
      o.put("locationContextEnabled", true);

      o.put("browserName", session.getCapabilities().getBundleName());
      o.put("browserVersion", session.getApplication().getCapabilities().getBundleVersion());

      o.put("platform", "IOS");
      o.put("platformName", "IOS");
      o.put("platformVersion", session.getCapabilities().getSDKVersion());

      o.put("javascriptEnabled", true);
      o.put("cssSelectors", true);
      o.put("takesElementScreenshot", false);

      o.put(IOSCapabilities.SIMULATOR, false);
      o.put(IOSCapabilities.DEVICE, session.getCapabilities().getDevice());
      o.put(IOSCapabilities.VARIATION, session.getCapabilities().getDeviceVariation());
      r.setValue(o);
      session.setCapabilityCachedResponse(r);
    }
  }

  @Override
  public int waitForProcessToDie() {
    return 0;
  }

  @Override
  public void stop() {
    service.stopSafari();
  }

  @Override
  public Response executeCommand(UIAScriptRequest request) {
    return null;
  }

  @Override
  public UIAutomationCommandExecutor getChannel() {
    return null;
  }

  @Override
  public TakeScreenshotService getScreenshotService() {
    return screenshotService;
  }


  static class ScreenshotSDK implements TakeScreenshotService {

    private final ScreenshotService service;

    public ScreenshotSDK(IOSDevice device) throws SDKException {
      service = new ScreenshotService(device);
    }

    @Override
    public String getScreenshot() {
      try {
        byte[] raw = service.takeScreenshot();
        return Base64.encodeBase64String(raw);
      } catch (Exception e) {
        throw new WebDriverException("Couldn't take the screenshot : " + e.getMessage(), e);
      }
    }
  }
}
