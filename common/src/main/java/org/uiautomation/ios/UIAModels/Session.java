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



public class Session {

  private final String opaqueKey;


  public Session(String key) {
    this.opaqueKey = key;
  }

  @SuppressWarnings("unused")
  private Session() {
    throw new IllegalAccessError();
  }



  public String getSessionId() {
    return opaqueKey;
  }


  @Override
  public String toString() {
    return opaqueKey;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((opaqueKey == null) ? 0 : opaqueKey.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Session other = (Session) obj;
    if (opaqueKey == null) {
      if (other.opaqueKey != null) return false;
    } else if (!opaqueKey.equals(other.opaqueKey)) return false;
    return true;
  }



}
