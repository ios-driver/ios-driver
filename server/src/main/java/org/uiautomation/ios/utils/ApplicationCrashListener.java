package org.uiautomation.ios.utils;


import org.uiautomation.ios.server.instruments.InstrumentsManager;

import java.util.logging.Logger;

public class ApplicationCrashListener implements CommandOutputListener {

  private static final Logger log = Logger.getLogger(DefaultCommandListener.class.getName());
  private final InstrumentsManager instrumentsManager;

  public ApplicationCrashListener(InstrumentsManager im){
    this.instrumentsManager = im;
  }

  @Override
  public void stdout(String log) {
    hasApplicationCrashed(log);
  }

  @Override
  public void stderr(String log) {
    hasApplicationCrashed(log);
  }

  private void hasApplicationCrashed(String log){
    if(log.contains("The target application appears to have died") || log.contains("Script was stopped by the user")){
      instrumentsManager.handleAppCrash(log);
    }
  }
}
