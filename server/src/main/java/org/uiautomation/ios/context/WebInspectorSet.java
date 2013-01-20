/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WebInspectorSet implements Set<WebInspector> {

  private Set<WebInspector> windows = new HashSet<WebInspector>();
  private WebInspector selected;

  public WebInspector getSelected() {
    return selected;
  }

  @Override
  public int size() {
    return windows.size();
  }

  @Override
  public boolean isEmpty() {
    return windows.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return windows.contains(o);
  }

  @Override
  public Iterator<WebInspector> iterator() {
    return windows.iterator();
  }

  @Override
  public Object[] toArray() {
    return windows.toArray();
  }

  @Override
  public <T> T[] toArray(T[] ts) {
    return windows.toArray(ts);
  }

  @Override
  public boolean add(WebInspector webWindowContext) {
    throw new RuntimeException("No op");
  }

  @Override
  public boolean remove(Object o) {
    throw new RuntimeException("No op");
  }

  @Override
  public boolean containsAll(Collection<?> objects) {
    throw new RuntimeException("No op");
  }

  @Override
  public boolean addAll(Collection<? extends WebInspector> webWindowContexts) {
    return windows.addAll(webWindowContexts);
  }

  @Override
  public boolean retainAll(Collection<?> objects) {
    throw new RuntimeException("No op");
  }

  @Override
  public boolean removeAll(Collection<?> objects) {
    return windows.removeAll(objects);
  }

  @Override
  public void clear() {
    throw new RuntimeException("No op");
  }
}
