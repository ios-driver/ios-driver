package org.uiautomation.ios.ide.views.positioning;

// device info. Base is protrait position.
public interface DevicePositioning {

  public int getFrameHeight();
  public int getFrameWidth();
  // from the top of the device.
  public int getScreenXoffset();
  //from the left of the device.
  public int getScreenYOffset();
  
}
