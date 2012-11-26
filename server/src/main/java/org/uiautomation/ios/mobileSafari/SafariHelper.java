package org.uiautomation.ios.mobileSafari;

import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.communication.IOSDevice;

public class SafariHelper {

  private final IOSDevice device;
  private final UIARect rect;

  public SafariHelper(IOSDevice device) {
    this.device = device;
    rect = getOffsets(device);
  }

  // x, y , h , w
  // x = no used
  // y = height of the address bar area
  // h = height of the device
  // w = width of the device
  // so for portait, the html page size = h-y
  // for landscape size = w-y
  private UIARect getOffsets(IOSDevice device) {
    switch (device) {
    case iPhoneSimulator:
      return new UIARect(0, 80, 480, 320);
    case iPadSimulator:
      return new UIARect(0, 96, 1024, 768);
    default:
      throw new RuntimeException(device + "offset not done");
    }
  }

  public int getPageWidth(Orientation o) {
    switch (o) {
    case UIA_DEVICE_ORIENTATION_PORTRAIT:
    case UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN:
      return rect.getWidth();
    case UIA_DEVICE_ORIENTATION_LANDSCAPELEFT:
    case UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT:
      return rect.getHeight();
    default:
      throw new RuntimeException("NI" + o);
    }
  }

  public int getAddressBarHeigth() {
    return rect.getY();
  }
}
