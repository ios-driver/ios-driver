package org.uiautomation.ios.server;

import org.uiautomation.ios.UIAModels.configuration.WorkingMode;


/**
 * state of the current application under test
 * 
 */
public class Context {
  private WorkingMode mode = WorkingMode.Native;
  private final DOMContext context;

  public Context(ServerSideSession session) {
    context = new DOMContext(session);
  }

  public void switchToMode(WorkingMode mode) {
    this.mode = mode;
  }

  public WorkingMode getWorkingMode() {
    return mode;
  }

  public void switchToFrame(String id) {
    
  }

  public DOMContext getDOMContext() {
    return context;
  }
}
