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

package org.uiautomation.ios.server.instruments;

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
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.instruments.communication.CommunicationChannel;
import org.uiautomation.ios.server.services.Instruments;
import org.uiautomation.ios.server.services.TakeScreenshotService;
import org.uiautomation.ios.server.simulator.InstrumentsFailedToStartException;
import org.uiautomation.ios.utils.Command;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

//import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi;

/**
 * An implementation of {@link Instruments} that does not use instruments.
 */
public class NoInstrumentsImplementationAvailable implements Instruments {

  private final Command installOpenUrlApp;
  private final Command runOpenUrlApp;
  private final String uuid;
  private final ScreenshotSDK screenshotService;
  private final IOSDevice device;

  public NoInstrumentsImplementationAvailable(String uuid) {
    this.uuid = uuid;
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
  }

  @Override
  public void stop() {

  }

  @Override
  public Response executeCommand(UIAScriptRequest request) {
    return null;
  }

  @Override
  public CommunicationChannel getChannel() {
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
//      System.out.println("base : "+raw.length);
      System.out.println("SDK : \t\t" + (System.currentTimeMillis() - start) + " ms");

      TiffImageParser parser = new TiffImageParser();
      try {
        start = System.currentTimeMillis();
        BufferedImage image = parser.getBufferedImage(raw, new HashMap<String, Object>());
        File f = new File("screen.png");
        System.out.println("loading :\t" + (System.currentTimeMillis() - start) + " ms");
        start = System.currentTimeMillis();
        ImageIO.write(image, "png", f);
        System.out.println("writing : \t" + (System.currentTimeMillis() - start) + " ms");
//        System.out.println("space : "+f.getTotalSpace());

      } catch (ImageReadException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      return Base64.encodeBase64String(raw);
    }
  }


  public static void main(String[] args) throws SDKException, LibImobileException {
    ScreenshotSDK
        s =
        new ScreenshotSDK(DeviceService.get("ff4827346ed6b54a98f51e69a261a140ae2bf6b3"));
    s.getPNGAsString();
    s.getPNGAsString();
    s.getPNGAsString();
    s.getPNGAsString();
  }
}
