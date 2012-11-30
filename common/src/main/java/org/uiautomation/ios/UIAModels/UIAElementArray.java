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
package org.uiautomation.ios.UIAModels;

import org.openqa.selenium.NoSuchElementException;
import org.uiautomation.ios.UIAModels.predicate.Criteria;

// TODO freynaud keep UIAElementArray at all ?
public interface UIAElementArray<T extends UIAElement> extends Iterable<T> {

  /**
   * the size of the collection
   * @return
   */
  public int size();

  /**
   * the elements with index in the collection. Starts at 0.
   * @param index
   * @return
   */
  public T get(int index);

  /**
   * @param criteria
   * @return the first element matching this criteria.
   */
  public T getFirst(Criteria criteria) throws NoSuchElementException;

  /**
   * return all 
   * @param criteria
   * @return
   */
  public UIAElementArray<T> getAll(Criteria criteria);


}
