package org.uiautomation.ios.UIAModels;


public enum Orientation {
  UIA_DEVICE_ORIENTATION_PORTRAIT(1,0),
  UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN(2,180),
  UIA_DEVICE_ORIENTATION_LANDSCAPELEFT(3,-90),
  UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT(4,90),
  UIA_DEVICE_ORIENTATION_FACEUP(-1,-1),
  UIA_DEVICE_ORIENTATION_FACEDOWN(-1,-1);

  private final int interfaceOrientation;
  private final int rotationInDegree;

  private Orientation(int interfaceOrientation,int rotationInDegree) {
    this.interfaceOrientation = interfaceOrientation;
    this.rotationInDegree= rotationInDegree;
  }
  
  public int getRotationInDegree(){
    return rotationInDegree;
  }

  public static Orientation fromInterfaceOrientation(int interfaceOrientation) {
    for (Orientation o : Orientation.values()) {
      if (o.interfaceOrientation == interfaceOrientation) {
        return o;
      }
    }
    throw new IllegalArgumentException("interfaceOrientation not recognized.");
  }
}
