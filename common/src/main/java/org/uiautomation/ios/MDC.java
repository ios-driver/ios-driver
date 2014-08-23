package org.uiautomation.ios;

/**
 * Created by freynaud on 22/08/2014.
 */
public class MDC {

  private static InheritableThreadLocal<String> CONTEXT = new InheritableThreadLocal<>();

  public void put(String session){
    CONTEXT.set(session);
  }

  public String get(){
    return CONTEXT.get();
  }
}
