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

public class RemoteObjectArray implements Iterable<Object> {

  private final RemoteObject remoteArray;
  private final int size;

  public RemoteObjectArray(RemoteObject remoteArray) {
    this.remoteArray = remoteArray;
    try {
      this.size = (Integer) remoteArray.call(".length");
    } catch (Exception e) {
      throw new RuntimeException("Error finding array size " + e.getMessage(), e);
    }
  }

  @Override
  public Iterator<Object> iterator() {
    return new RemoteObjectIterator(remoteArray, size);
  }
}
