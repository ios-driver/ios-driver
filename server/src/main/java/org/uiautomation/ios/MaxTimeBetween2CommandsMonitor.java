package org.uiautomation.ios;



import java.util.logging.Logger;


public class MaxTimeBetween2CommandsMonitor implements ServerSideSessionMonitor {

  private final ServerSideSession session;
  private boolean shouldRun = true;
  private Thread loop;
  private static final Logger log = Logger.getLogger(MaxTimeBetween2CommandsMonitor.class.getName());

  public MaxTimeBetween2CommandsMonitor(ServerSideSession session) {
    this.session = session;
  }

  @Override
  public void startMonitoring() {
    synchronized (session) {
      shouldRun = true;
    }

    loop = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          runLoop();
        } catch (InterruptedException ignore) {
          //ignore.printStackTrace();
        }

      }
    });
    loop.start();

  }

  private void runLoop() throws InterruptedException {
    while (shouldRun()) {
      Thread.sleep(100);
      if (session.hasTimedOutBetween2Commands()) {
        log.warning("Session has timed out. time out between 2 commands");
        session.stop(ServerSideSession.StopCause.timeOutBetweenCommand);
      }
    }
  }

  private boolean shouldRun() {
    synchronized (session) {
      return shouldRun;
    }
  }

  @Override
  public void stopMonitoring() {
    synchronized (session) {
      shouldRun = false;
    }
    loop.interrupt();
  }
}
