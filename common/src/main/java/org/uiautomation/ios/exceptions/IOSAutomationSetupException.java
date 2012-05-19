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


public class IOSAutomationSetupException extends IOSAutomationException {

  private static final long serialVersionUID = 7156695181928204291L;

  public IOSAutomationSetupException(String message) {
    super(message);
  }
  public IOSAutomationSetupException(Throwable t) {
    super(t);
  }

  public IOSAutomationSetupException(String message, Throwable t) {
    super(message, t);
  }

}
