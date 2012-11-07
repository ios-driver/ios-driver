package org.uiautomation.ios.webInspector.DOM;

import java.util.Iterator;

public class RemoteObjectIterator implements Iterator<RemoteObject> {


  private final RemoteObject underlyingObject;
  private int index = 0;
  private final int size;


  public RemoteObjectIterator(RemoteObject uro, int size) {
    this.underlyingObject = uro;
    this.size = size;
  }

  @Override
  public boolean hasNext() {
    return index < size;
  }

  @Override
  public RemoteObject next() {
    RemoteObject res;
    try {
      res = underlyingObject.call("[" + index + "]");
      index++;
      return res;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
   
  }

  @Override
  public void remove() {
    throw new IllegalAccessError("NI");
  }

}
