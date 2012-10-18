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
package org.uiautomation.ios.exceptions;

public enum ExceptionStatus {
  NoSuchElement(7, NoSuchElementException.class), 
  StaleElementReference(10,StaleReferenceException.class),
  ElementNotVisible(11, ElementNotVisibleException.class), 
  UnknownError(13,IOSAutomationException.class),
  UnexpectedAlertOpen(26,UnexpectedAlertOpen.class),
  NoAlertOpenError(27,NoAlertOpenError.class),
  InvalidSelector(32,InvalidCriteriaException.class),
  SessionNotCreatedException(33,SessionNotCreatedException.class);

  private final Class<? extends Exception> ex;
  private final int status;

  private ExceptionStatus(int status, Class<? extends Exception> ex) {
    this.ex = ex;
    this.status = status;
  }


  public static Class<? extends Exception> getExceptionForStatus(int status) {
    for (ExceptionStatus e : values()) {
      if (e.status == status) {
        return e.ex;
      }
    }
    return UnknownError.ex;
  }


}
