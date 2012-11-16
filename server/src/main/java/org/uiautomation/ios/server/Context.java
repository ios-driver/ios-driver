package org.uiautomation.ios.server;


/**
 * state of the current application under test
 * 
 */
public class Context {
  private WorkingMode mode = WorkingMode.Native;
  private final DOMContext context;

  public Context() {
    context = new DOMContext();
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
