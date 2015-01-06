/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.utils;

import com.google.common.base.Joiner;

import org.openqa.selenium.WebDriverException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Command {

  private static final Logger log = Logger.getLogger(Command.class.getName());

  private final List<String> args;
  private final List<String> out = new CopyOnWriteArrayList<String>();
  private final List<String> err = new CopyOnWriteArrayList<String>();
  private final
  List<CommandOutputListener>
      listeners =
      new CopyOnWriteArrayList<CommandOutputListener>();
  private volatile Process process;
  private List<Thread> threads = new ArrayList<Thread>();
  private File workingDir = null;
  private Map<String, String> environment;

  public Command(List<String> args, boolean logToConsole) {
    this.args = args;
    environment = new Hashtable<>();
    if (logToConsole) {
      listeners.add(new DefaultCommandListener(commandString()));
    }
  }

  /**
   * Execute the command, and wait for it to finish. Also wait for stdout and stderr listener to finish processing their streams.
   */
  public int executeAndWait() {
    return executeAndWait(false);
  }

  public int executeAndWait(boolean ignoreErrors) {
    start();
    int exitCode = waitFor(-1);
    if (!ignoreErrors && exitCode != 0) {
      throw new WebDriverException(
          "execution failed. Exit code =" + exitCode + " , command was: " + commandString());
    }
    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        throw new WebDriverException(e);
      }
    }
    return exitCode;
  }

  /**
   * Starts the command. Doesn't wait for it to finish. Doesn't wait for stdout and stderr either.
   */
  public void start() {
    if (log.isLoggable(Level.FINE)) {
      log.fine(String.format("Starting command: %s", commandString()));
    }
    ProcessBuilder builder = new ProcessBuilder(args);
    if (workingDir != null) {
      builder.directory(workingDir);
    }
    if (!environment.isEmpty()) {
      Map<String, String> env = builder.environment();
      env.putAll(environment);
    }

    try {
      process = builder.start();
    } catch (IOException e) {
      throw new WebDriverException("failed to start process " + args, e);
    }

    threads.add(listen(process.getInputStream(), out, true));
    threads.add(listen(process.getErrorStream(), err, false));
  }

  /**
   * @param maxWaitMillis max time to wait for the command to finish, -1 for not limit
   */
  public int waitFor(final int maxWaitMillis) {
    Timer forceStopTimer = null;

    try {
      if (maxWaitMillis > 0) {
        forceStopTimer = new Timer(true);
        forceStopTimer.schedule(new TimerTask() {
          @Override
          public void run() {
            log.warning("destroying process after waiting for " + maxWaitMillis + "ms: " + commandString());
            process.destroy();
          }
        }, maxWaitMillis);
      }
      return process.waitFor();
    } catch (InterruptedException e) {

      throw new WebDriverException("error waiting for " + args + " to finish.", e);
    } finally {
      if (forceStopTimer != null) {
        forceStopTimer.cancel();
      }
    }
  }

  public void registerListener(CommandOutputListener l) {
    listeners.add(l);
  }

  private Thread listen(final InputStream in, final List<String> out, final boolean normal) {
    Thread t = new Thread() {
      @Override
      public void run() {
        BufferedReader reader = null;
        try {
          reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException ignore) {
        }
        String line;
        try {
          while ((line = reader.readLine()) != null) {
            add(line, normal);
          }
        } catch (IOException e) {
          if (!"Stream closed".equals(e.getMessage())) {
            log.warning("Error reading the output of the process: " + e);
          }
        }
      }
    };
    t.start();
    return t;
  }

  private void add(String line, boolean normal) {
    if (log.isLoggable(Level.FINER)) {
      log.finer(String.format("%s %s: %s", args.get(0), normal ? "stdout" : "stderr", line));
    }
    if (normal) {
      out.add(line);
    } else {
      err.add(line);
    }

    for (CommandOutputListener l : listeners) {
      if (normal) {
        l.stdout(line);
      } else {
        l.stderr(line);
      }
    }
  }

  public List<String> getStdOut() {
    return out;
  }

  public List<String> getErr() {
    return err;
  }

  public void forceStop() {
    if (process != null) {
      process.destroy();
    }
    for (Thread t : threads) {
      t.interrupt();
    }
  }

  public String commandString() {
    return Joiner.on(" ").join(args);
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append(commandString());
    b.append("\n\n");
    b.append(getStdOut());
    b.append("\n\n");
    b.append(getErr());

    return b.toString();
  }

  /**
   * set the working directory where the output files will be written.
   */
  public void setWorkingDirectory(File output) {
    this.workingDir = output;
  }

  public void addEnvironment(String key, String value) {
    environment.put(key, value);
  }

}
