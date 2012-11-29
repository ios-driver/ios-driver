/*
 * Copyright 2012 ios-driver committers.
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
package org.uiautomation.ios.webInspector.DOM;

import java.util.Iterator;

public class RemoteObjectIterator implements Iterator<Object> {

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
  public Object next() {
    Object res;
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
