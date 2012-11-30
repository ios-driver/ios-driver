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

package org.uiautomation.ios.server.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openqa.selenium.WebDriverException;

public class Command {

  private final List<String> args;
  private volatile Process process;
  private final List<String> out = new CopyOnWriteArrayList<String>();
  private final List<String> err = new CopyOnWriteArrayList<String>();
  private final List<CommandOutputListener> listeners = new CopyOnWriteArrayList<CommandOutputListener>();
  private List<Thread> threads = new ArrayList<Thread>();

  private File workingDir = null;

  public Command(List<String> args, boolean logToConsole) {
    this.args = args;
    if (logToConsole) {
      listeners.add(new DefaultCommandListener());
    }

  }

  /**
   * execute the command, and wait for it to finish. Also wait for stdout and
   * std err listener to finish processing their streams.
   * 
   * @throws IOSAutomationSetupException
   */
  public void executeAndWait() {
    start();
    int exitCode = waitFor();
    if (exitCode != 0) {
      throw new WebDriverException("execution failed. Exit code =" + exitCode + " , command was : " + args);
    }
    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        throw new WebDriverException(e);
      }
    }

  }

  /**
   * starts the command. Doesn't wait for it to finish.Doesn't wait for stdout
   * and stderr either.
   * 
   * @throws IOSAutomationSetupException
   */
  public void start() {
    ProcessBuilder builder = new ProcessBuilder(args);
    if (workingDir != null) {
      builder.directory(workingDir);
    }

    try {
      process = builder.start();
    } catch (IOException e) {
      throw new WebDriverException("failed to start process " + args, e);
    }

    final InputStream normal = process.getInputStream();
    final InputStream error = process.getErrorStream();

    threads.add(listen(normal, out, true));
    threads.add(listen(error, err, false));

  }

  public int waitFor() {
    try {
      return process.waitFor();
    } catch (InterruptedException e) {
      throw new WebDriverException("error waiting for " + args + " to finish.", e);
    }
  }

  public void registerListener(CommandOutputListener l) {
    listeners.add(l);
  }

  private Thread listen(final InputStream in, final List<String> out, final boolean normal) {
    Thread t = new Thread(new Runnable() {
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
          e.printStackTrace();
        }
      }
    });
    t.start();
    return t;
  }

  private void add(String line, boolean normal) {
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

  public String toString() {
    StringBuilder b = new StringBuilder();
    for (String s : args) {
      b.append(" " + s);
    }
    b.append("\n\n");
    b.append(getStdOut());
    b.append("\n\n");
    b.append(getErr());

    return b.toString();

  }

  /**
   * set the working directory where the output files will be written.
   * 
   * @param output
   */
  public void setWorkingDirectory(File output) {
    this.workingDir = output;
  }

}
