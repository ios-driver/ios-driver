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
package org.uiautomation.ios.UIAModels.predicate;

import org.openqa.selenium.InvalidSelectorException;

public enum CompositionType {
  AND, OR, NOT;


  @SuppressWarnings("unchecked")
  public Class<? extends ComposedCriteria> getAssociatedClass() {
    String pack = this.getDeclaringClass().getPackage().getName();
    String camel =
        this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
    String clazz = pack + "." + camel + "Criteria";
    try {
      return (Class<? extends ComposedCriteria>) Class.forName(clazz);
    } catch (ClassNotFoundException e) {
      throw new InvalidSelectorException(clazz + " isn't a valid class.", e);
    }
  }
}
