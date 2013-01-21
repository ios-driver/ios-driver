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
package org.uiautomation.ios.server;

import org.uiautomation.ios.UIAModels.configuration.WorkingMode;


/**
 * state of the current application under test
 */
public class Context {

  private WorkingMode mode = WorkingMode.Native;
  //private final DOMContext context;

  public Context(ServerSideSession session) {
    //context = new DOMContext(session);
  }

  public void switchToMode(WorkingMode mode) {
    this.mode = mode;
  }

  public WorkingMode getWorkingMode() {
    return mode;
  }

  public void switchToFrame(String id) {

  }

  /*public DOMContext getDOMContext() {
    return context;
  } */
}
