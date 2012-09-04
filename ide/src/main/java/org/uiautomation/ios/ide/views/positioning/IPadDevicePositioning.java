package org.uiautomation.ios.ide.views.positioning;

import org.uiautomation.ios.UIAModels.Orientation;


public class IPadDevicePositioning implements DevicePositioning {

  private final int IPAD_FRAME_H = 1108;
  private final int IPAD_FRAME_W = 852;

  private final int IPAD_SCREEN_H = 1024;
  private final int IPAD_SCREEN_W = 768;

  private final int FRAME_TO_SCREEN_T = 42;

  private final int FRAME_TO_SCREEN_L = 42;

  private final int marginTop;
  private final int marginLeft;

  private final Orientation orientation;


  public IPadDevicePositioning(Orientation o, int marginTop, int marginLeft) {
    this.marginTop = marginTop;
    this.marginLeft = marginLeft;
    this.orientation = o;
  }

  @Override
  public int getFrameHeight() {
    return IPAD_FRAME_H;
  }

  @Override
  public int getFrameWidth() {
    return IPAD_FRAME_W;
  }

  @Override
  public int getScreenXoffset() {
    return marginTop;
  }

  @Override
  public int getScreenYOffset() {
    return marginLeft;
  }


  public int getFrameLeft() {
    switch (orientation) {
      case UIA_DEVICE_ORIENTATION_LANDSCAPELEFT:// -90
        return IPAD_FRAME_H / 2 - IPAD_FRAME_W / 2 + marginTop;
      case UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT:// +90
        return IPAD_FRAME_H / 2 - IPAD_FRAME_W / 2 + marginTop;
      case UIA_DEVICE_ORIENTATION_PORTRAIT:
        return marginLeft;
      default:
        throw new RuntimeException("NI " + orientation);
    }


  }

  public int getFrameTop() {
    switch (orientation) {
      case UIA_DEVICE_ORIENTATION_LANDSCAPELEFT:// -90
        return -IPAD_FRAME_H / 2 + IPAD_FRAME_W / 2 + marginLeft;
      case UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT:// +90
        return -IPAD_FRAME_H / 2 + IPAD_FRAME_W / 2 + marginLeft;
      case UIA_DEVICE_ORIENTATION_PORTRAIT:
        return marginTop;
      default:
        throw new RuntimeException("NI " + orientation);
    }

  }

  public int getScreenTop() {
    switch (orientation) {
      case UIA_DEVICE_ORIENTATION_LANDSCAPELEFT:// -90
        return -IPAD_SCREEN_H / 2 + IPAD_SCREEN_W / 2 + marginTop + FRAME_TO_SCREEN_L;
      case UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT:// +90
        return -IPAD_SCREEN_H / 2 + IPAD_SCREEN_W / 2 + marginTop + FRAME_TO_SCREEN_L;
      case UIA_DEVICE_ORIENTATION_PORTRAIT:
        return marginTop + FRAME_TO_SCREEN_T;
      default:
        throw new RuntimeException("NI " + orientation);
    }

  }

  public int getScreenLeft() {
    switch (orientation) {
      case UIA_DEVICE_ORIENTATION_LANDSCAPELEFT:// -90
        return IPAD_SCREEN_H / 2 - IPAD_SCREEN_W / 2 + marginTop + FRAME_TO_SCREEN_T;
      case UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT:// +90
        return IPAD_SCREEN_H / 2 - IPAD_SCREEN_W / 2 + marginTop
            + (IPAD_FRAME_H - IPAD_SCREEN_H - FRAME_TO_SCREEN_T);
      case UIA_DEVICE_ORIENTATION_PORTRAIT:
        return marginLeft + FRAME_TO_SCREEN_L;
      default:
        throw new RuntimeException("NI " + orientation);
    }

  }

  public int getScreenHeigth() {
    return IPAD_SCREEN_H;
  }

  public int getScreenWidth() {
    return IPAD_SCREEN_W;
  }

  public int getRealScreenLeft() {
    return marginLeft + FRAME_TO_SCREEN_L;
  }

  public int getRealScreenTop() {
    return marginTop + FRAME_TO_SCREEN_T;
  }

  public int getRealScreenHeight() {
    switch (orientation) {
      case UIA_DEVICE_ORIENTATION_LANDSCAPELEFT:// -90
      case UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT:// +90
        return IPAD_SCREEN_W;
      case UIA_DEVICE_ORIENTATION_PORTRAIT:
      case UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN:
        return IPAD_SCREEN_H;
      default:
        throw new RuntimeException("NI " + orientation);
    }
  }

  public int getRealScreenWidth() {
    switch (orientation) {
      case UIA_DEVICE_ORIENTATION_LANDSCAPELEFT:// -90
      case UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT:// +90
        return IPAD_SCREEN_H;
      case UIA_DEVICE_ORIENTATION_PORTRAIT:
      case UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN:
        return IPAD_SCREEN_W;
      default:
        throw new RuntimeException("NI " + orientation);
    }
  }



}
