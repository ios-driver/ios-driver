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

package org.uiautomation.ios.server.utils.hack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriverException;


public class TimeSpeeder implements HorribleHack {
  
  private static final Logger log = Logger.getLogger(TimeSpeeder.class.getName());

  private final static TimeSpeeder INSTANCE = new TimeSpeeder();
  private Thread t;
  private volatile boolean active = false;
  private volatile boolean on = true;

  public static TimeSpeeder getInstance() {
    return INSTANCE;
  }

  private synchronized void setActive(boolean active) {
    this.active = active;
  }

  public synchronized boolean isActive() {
    return active;
  }
  
  /**
   * if the time goes faster on the computer, needs to compensate 
   * for the timeout to have something close to what the use wants.
   * @return
   */
  public synchronized float getSecondDuration(){
    if (isActive()){
      return 300/23;
    }else {
      return 1;
    }
  }

  public void start() {
    if (on) {
      setActive(true);
      t = new Thread(new Runnable() {

        public void run() {
          try {
            speedTime();
          } catch (Exception ignore) {

          }
        }
      });
      t.start();
    }
  }

  public void stop() {
    if (isActive()) {
      setActive(false);
      t.interrupt();
    }

  }

  private void speedTime() throws Exception {
    log.info("Speeding time");
    long time = System.currentTimeMillis();
    int i = 0;
    int delta = 1000;
    while (isActive()) {
      i++;
      Thread.sleep(50);
      long newTime = time + (i * delta);
      Date d = new Date(newTime);
      List<String> c = new ArrayList<String>();
      c.add("sudo");
      c.add("/bin/date");
      // TODO freynaud
      c.add(get(d.getHours()) + get(d.getMinutes()) + "." + get(d.getSeconds()));
      ProcessBuilder builder = new ProcessBuilder(c);
      Process p = builder.start();
      int exit = p.waitFor();
      if (exit != 0) {
        throw new WebDriverException("couldn't set time" + c);
      }
    }
  }

  private static String get(int o) {
    return o < 10 ? "0" + o : "" + o;
  }

  public void activate() {
    this.on = true;

  }

  public void desactivate() {
    this.on = false;

  }
}
