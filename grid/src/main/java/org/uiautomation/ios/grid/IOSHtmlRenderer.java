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
package org.uiautomation.ios.grid;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.internal.utils.HtmlRenderer;

import static org.openqa.grid.common.RegistrationRequest.APP;
import static org.openqa.grid.common.RegistrationRequest.BROWSER;

public class IOSHtmlRenderer implements HtmlRenderer {

  private IOSRemoteProxy proxy;

  public IOSHtmlRenderer(IOSRemoteProxy proxy) {
    this.proxy = proxy;
  }

  public String renderSummary() {
    StringBuilder builder = new StringBuilder();
    builder.append("<fieldset>");
    builder.append("<legend>").append(proxy.getClass().getSimpleName()).append("</legend>");
    if (proxy.isRestarting()) {
      builder.append("<b>***RESTARTING***</b><br/>");
    }
    builder.append("Listening on ").append(proxy.getRemoteHost());
    if (proxy.getTimeOut() > 0) {
      int inSec = proxy.getTimeOut() / 1000;
      builder.append(". Test session time out after ").append(inSec).append(" sec.");
    }

    builder.append("<br>Supports up to <b>").append(proxy.getMaxNumberOfConcurrentTestSessions())
        .append("</b> concurrent tests from : </u><br>");

    for (TestSlot slot : proxy.getTestSlots()) {
      boolean simulator = Boolean.parseBoolean(slot.getCapabilities().get("simulator").toString());
      if (simulator) {
        builder.append(slot.getCapabilities().containsKey(BROWSER) ? slot.getCapabilities().get(
            BROWSER) : slot.getCapabilities().get(APP));
      } else {
        builder.append(slot.getCapabilities().get("device")).append(" [").append(slot.getCapabilities().get("sdkVersion")).append("]");
      }
      TestSession session = slot.getSession();
      try {
        if (slot.getCapabilities().containsKey("CFBundleExecutable")){
          if (!slot.getCapabilities().get("CFBundleExecutable").toString().equalsIgnoreCase("MobileSafari")){
            builder.append("<img src=\"" + proxy.getRemoteHost() + getIconUrl(slot) + "\" title=\"" + slot.getCapabilities().get("CFBundleExecutable") + "\" alt=\"" + slot.getCapabilities().get("CFBundleExecutable") + "\" height=\"30\" width=\"30\">");
          } else {
            builder.append("<b> *safari* </b>");
          }

        }

      } catch (JSONException ignored) {
      }
      if (simulator) {
        builder.append(" ").append(slot.getCapabilities().get("device_Alt")).append(" - ")
            .append(slot.getCapabilities().get("sdkVersion_Alt")).append("");
      }
      builder.append(session == null ? " (free)" : "(busy, session " + session + ")");
      builder.append("<br/>");
    }
    builder.append("</fieldset>");
    return builder.toString();
  }

  private String getIconUrl(TestSlot slot) throws JSONException {
    JSONObject resources = (JSONObject) slot.getCapabilities().get("resources");
    return resources.getString("CFBundleIconFile");
  }
}