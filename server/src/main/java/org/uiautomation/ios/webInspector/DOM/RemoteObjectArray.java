package org.uiautomation.ios.webInspector.DOM;

import java.util.Iterator;

public class RemoteObjectArray implements Iterable<Object> {

  private final RemoteObject remoteArray;
  private final int size;

  public RemoteObjectArray(RemoteObject remoteArray) {
    this.remoteArray = remoteArray;
    try {
      this.size = (Integer)remoteArray.call(".length");
    } catch (Exception e) {
      throw new RuntimeException("Error finding array size " + e.getMessage(), e);
    }
  }

  @Override
  public Iterator<Object> iterator() {
    return new RemoteObjectIterator(remoteArray, size);
  }
}
