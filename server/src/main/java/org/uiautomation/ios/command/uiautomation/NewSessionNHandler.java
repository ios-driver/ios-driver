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
package org.uiautomation.ios.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.SessionNotInitializedException;
import org.uiautomation.ios.command.BaseNativeCommandHandler;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.drivers.RecoverableCrashException;
import org.uiautomation.ios.instruments.ApplicationCrashedOnStartException;
import org.uiautomation.ios.instruments.InstrumentsFailedToStartException;
import org.uiautomation.ios.utils.ServerIsShutingDownException;

import java.util.logging.Logger;

public final class NewSessionNHandler extends BaseNativeCommandHandler {

  private static final Logger log = Logger.getLogger(NewSessionNHandler.class.getName());
  private long TIMEOUT_SEC;
  private static final long MAX_RETRIES = 3;


  public NewSessionNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    TIMEOUT_SEC = driver.getIOSServerConfiguration().getNewSessionTimeoutSec();
  }

  @Override
  public Response handle() throws Exception {

    ServerSideSession session = null;
    try {
      JSONObject payload = getRequest().getPayload();
      IOSCapabilities cap = new IOSCapabilities(payload.getJSONObject("desiredCapabilities"));

      long timeOut = TIMEOUT_SEC;
      for (int i = 0; i < MAX_RETRIES; i++) {
        session = safeStart(timeOut, cap);
        timeOut = (i + 2) * TIMEOUT_SEC;

        if (session != null) {
          break;
        }
      }

      if (session == null) {
        throw new SessionNotCreatedException(
            "failed starting after " + MAX_RETRIES + " retries.Final wait was " + timeOut);
      }

      Response resp = new Response();
      resp.setSessionId(session.getSessionId());
      resp.setStatus(0);
      resp.setValue("");
      return resp;
    } catch (Exception e) {
      if (session != null) {
        session.stop();
      }
      if (e instanceof WebDriverException) {
        throw e;
      } else {
        throw new SessionNotCreatedException(e.getMessage(), e);
      }
    }
  }

  private ServerSideSession safeStart(long timeOut, IOSCapabilities cap)
      throws InstrumentsFailedToStartException, ApplicationCrashedOnStartException {
    ServerSideSession session = null;
    try {
      // init session
      session = getServer().createSession(cap);
      if (session == null) {
        throw new ServerIsShutingDownException(
            "The server is currently shutting down and doesn't accept new tests.");
      }

      // start session
      session.start(timeOut);
      return session;
    } catch (SessionNotInitializedException e) {
      log.info("The server cannot run " + cap + " at the moment." + e.getMessage());
      throw new SessionNotCreatedException("The server cannot run " + cap + " at the moment." + e.getMessage());
    } catch (InstrumentsFailedToStartException|RecoverableCrashException e) {
      log.warning("Instruments failed to start in the allocated time ( " + timeOut + "sec):" + e
              .getMessage());
      if (session != null) {
        session.stop();
      }
    } catch (ApplicationCrashedOnStartException e) {
      log.warning("Application crashed while instruments was launching:" + e);
      session.stop(ServerSideSession.StopCause.crash);
      throw e;
    } catch (Exception e) {
      log.warning("Error starting the session." + e.getMessage());
      if (session != null) {
        session.stop();
      }
      throw e;
    } finally {
      if (session != null && session.getStopCause() != null) {
        log.warning("app has crashed at startup :" + session.getStopCause());
      }
    }
    return null;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
