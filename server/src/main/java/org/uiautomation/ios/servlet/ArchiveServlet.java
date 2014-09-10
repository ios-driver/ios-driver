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

package org.uiautomation.ios.servlet;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ArchiveServlet extends DriverBasedServlet {

  private static final Logger log = Logger.getLogger(ArchiveServlet.class.getName());
  private final Map<String, ArchiveStatus>
      statuses =
      new ConcurrentHashMap<String, ArchiveStatus>();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    try {
      String logId = req.getParameter("logId");
      if (logId != null) {
        response.getWriter().write(status(logId));
      } else {
        response.getWriter().write(page());
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    response.getWriter().close();
  }


  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    //LoggerService.registerListener(new ArchiveActivityListener());

    statuses.clear();

    StringBuffer jb = new StringBuffer();
    String line = null;
    try {
      BufferedReader reader = req.getReader();
      while ((line = reader.readLine()) != null) {
        jb.append(line);
      }
    } catch (Exception e) { /*report an error*/ }

    try {
      JSONObject json = new JSONObject(jb.toString());
      final String bundleId = json.getString("bundleId");
      final String uuid = json.getString("uuid");
      /*final DeviceInstallerService
          service =
          new DeviceInstallerService(uuid);
      final File archiveFolder = getServer().getApplicationStore().getArchiveFolder();
      new Thread(new Runnable() {
        @Override
        public void run() {
          service.archive(bundleId, false, false, archiveFolder
              , true);
        }
      }).start();*/

      response.getWriter().write(uuid);
      response.getWriter().close();
    } catch (Exception e) {
      // crash and burn
      throw new IOException("Error parsing JSON request string");
    }


  }

  private String status(String logId) {
    //return getStatus(logId).getStatus().toString();
    return null;
  }

  private String page() throws JSONException {
    return "content";
  }


  /*class ArchiveActivityListener implements JNILoggerListener {

    @Override
    public void onLog(int level, String message) {
      if (message.contains("Archive")) {
        Message m = new Message(message);
        ArchiveStatus status = getStatus(m.getLogId());
        status.update(m);
      }
    }


  }

  private ArchiveStatus getStatus(String logId) {
    ArchiveStatus res = statuses.get(logId);
    if (res == null) {
      res = new ArchiveStatus(logId);
      statuses.put(logId, res);
    }
    return res;
  }*/

  class ArchiveStatus {

    private int id = 0;
    private String currentPhase;
    private String currentStep;
    private int progress;
    private final String logId;


    public ArchiveStatus(String logId) {
      this.logId = logId;
    }

    public void update(Message message) {
      id++;
      currentPhase = message.getPhase();
      currentStep = message.getStep();
      if (message.getProgress() != -1) {
        progress = message.getProgress();
      }
    }

    public JSONObject getStatus() {
      JSONObject res = new JSONObject();
      try {
        res.put("id", id);
        res.put("phase", currentPhase);
        res.put("step", currentStep);
        res.put("progress", progress);
        res.put("logId", logId);
      } catch (JSONException e) {
        log.log(Level.SEVERE, "format error", e);
      }
      return res;
    }

  }
  // Archive
  //  TakingInstallLock
  //  EmptyingApplication
  //  ArchivingApplication
  //  Complete 93%

  // ArchiveCopy -- 95%

  // RemoveArchive
  //  RemovingArchive 96%
  //  Complete 100%
}


class Message {

  private String logId;
  private String phase;
  private String step;
  private int progress;
  private String raw;


  public Message(String msg) {

    try {
      raw = msg.replaceAll("(\\r|\\n)", "");
      logId = getLogId(raw);
      phase = getPhase(raw);
      step = getStep(raw);
      progress = getProgress(raw, phase, step);
    } catch (Exception e) {
      System.err.println(msg);
      progress = -1;
    }
  }

  private int getProgress(String msg, String phase, String step) {
    if ("Error occured:".equals(step)) {
      return 100;
    }

    if ("Archive".equals(phase)) {
      if ("Complete".equals(step)) {
        return 93;
      } else {
        String s = msg.split(";")[3];
        return Integer.parseInt(s.replace("%", ""));
      }
    } else if ("ArchiveCopy".equals(phase)) {
      return 95;
    } else if ("RemoveArchive".equals(phase)) {
      if ("RemovingArchive".equals(step)) {
        return 96;
      } else {
        return 100;
      }
    } else {
      throw new WebDriverException("not recognized : " + phase);
    }
  }


  private String getPhase(String message) {
    return message.split(";")[1];
  }

  private String getStep(String message) {
    return message.split(";")[2];
  }

  private String getLogId(String message) {
    return message.split(";")[0];
  }

  public String getLogId() {
    return logId;
  }

  public String getPhase() {
    return phase;
  }

  public String getStep() {
    return step;
  }

  public int getProgress() {
    return progress;
  }

  @Override
  public String toString() {
    return raw + "-->id:" + logId + ",phase:" + phase + ",step:" + step + ",progress:" + progress;
  }
}