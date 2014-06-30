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

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.binary.Base64;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.formats.tiff.TiffImageParser;
import org.libimobiledevice.ios.driver.binding.exceptions.LibImobileException;
import org.libimobiledevice.ios.driver.binding.exceptions.SDKException;
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
import org.uiautomation.ios.utils.Command;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi;

/**
 * An implementation of {@link Instruments} that does not use instruments.
 */
public class NoInstrumentsImplementationAvailable implements Instruments {

  private final Command installOpenUrlApp;
  private final Command runOpenUrlApp;
  private final ServerSideSession session;
  private final ScreenshotSDK screenshotService;
  private final IOSDevice device;

  /**
   * this implementation is only relevant for real device safari, when instruments cannot be used.
   */
  @Override
  public boolean isCompatible(ServerSideSession session) {
    return session.isSafariRealDevice();
  }

  public NoInstrumentsImplementationAvailable(ServerSideSession session) {
    this.session = session;
    String uuid = ((RealDevice) session.getDevice()).getUuid();
    try {
      device = DeviceService.get(uuid);
      screenshotService = new ScreenshotSDK(device);
    } catch (SDKException | LibImobileException e) {
      throw new WebDriverException(
          "Cannot init screenshot service for " + uuid + " : " + e.getMessage());
    }

    installOpenUrlApp = new Command(ImmutableList.of(
        "ideviceinstaller", "-i", "openURL.ipa", "-u", uuid), true);
    runOpenUrlApp = new Command(ImmutableList.of(
        "idevice-app-runner", "-s", "com.google.openURL", "-u", uuid), true);

  }

  @Override
  public void start(long timeOut) throws InstrumentsFailedToStartException {
    // Try running the openURL app once, ignoring errors.
    // If running openURL failed, reinstall the app and try again.
    //int exitCode = runOpenUrlApp.executeAndWait(/*ignoreErrors*/ true);
    //if (exitCode != 0) {
    //  installOpenUrlApp.executeAndWait();
    //  runOpenUrlApp.executeAndWait();
    //}
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
  public void stop() {

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


  private static class IDeviceScreenshotService implements TakeScreenshotService {
    //private static final TIFFImageWriterSpi TIFF_WRITER = new TIFFImageWriterSpi();

    private final String uuid;

    private IDeviceScreenshotService(String uuid) {
      this.uuid = uuid;
    }

    @Override
    public String getScreenshot() {

      ensureTiffWriterIsRegistered();
      File screenshotFile = null;
      ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();

      try {
        screenshotFile = File.createTempFile("screenshot", ".tiff");
        Command takeScreenshot = new Command(ImmutableList.of(
            "idevicescreenshot", "-u", uuid, screenshotFile.getAbsolutePath()), true);
        takeScreenshot.executeAndWait();
        BufferedImage image = ImageIO.read(screenshotFile);
        ImageIO.write(image, "png", imageBytes);
      } catch (IOException | WebDriverException e) {
        throw new IllegalStateException("Unable to take screenshot", e);
      } finally {
        if (screenshotFile != null) {
          screenshotFile.delete();
        }
      }

      return Base64.encodeBase64String(imageBytes.toByteArray());
    }

    private static void ensureTiffWriterIsRegistered() {
      IIORegistry registry = IIORegistry.getDefaultInstance();
      /*if (!registry.contains(TIFF_WRITER)) {
        registry.registerServiceProvider(TIFF_WRITER);
      }*/
    }
  }

  static class ScreenshotSDK implements TakeScreenshotService {

    private final ScreenshotService service;

    public ScreenshotSDK(IOSDevice device) throws SDKException {
      service = new ScreenshotService(device);
    }

    @Override
    public String getScreenshot() {
      try {
        return getPNGAsString();
      } catch (Exception e) {
        throw new WebDriverException("Couldn't take the screenshot : " + e.getMessage(), e);
      }
    }


    private String getPNGAsString() throws SDKException {
      long start = System.currentTimeMillis();
      byte[] raw = service.takeScreenshot();

      TiffImageParser parser = new TiffImageParser();
      try {
        start = System.currentTimeMillis();
        BufferedImage image = parser.getBufferedImage(raw, new HashMap<String, Object>());
        System.out.println("loading :\t" + (System.currentTimeMillis() - start) + " ms");
        start = System.currentTimeMillis();
        byte[] pngBytes = getBufferedImageAsPngBytes(image);
        System.out.println("writing : \t" + (System.currentTimeMillis() - start) + " ms");
//      System.out.println("space : "+f.getTotalSpace());
        return Base64.encodeBase64String(pngBytes);
      } catch (ImageReadException | IOException e) {
          throw new IllegalStateException("Unable to take screenshot", e);
      }
    }

    private byte[] getBufferedImageAsPngBytes(BufferedImage image) throws IOException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ImageIO.write(image, "png", outputStream);
      return outputStream.toByteArray();
    }

  }

  public static void main(String[] args) throws Exception {
    ScreenshotSDK
        s =
        new ScreenshotSDK(DeviceService.get("ff4827346ed6b54a98f51e69a261a140ae2bf6b3"));
    s.getPNGAsString();
    s.getPNGAsString();
    s.getPNGAsString();
    s.getPNGAsString();
  }
}
