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

import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.internal.utils.HtmlRenderer;

import static org.openqa.grid.common.RegistrationRequest.APP;
import static org.openqa.grid.common.RegistrationRequest.BROWSER;

public class IOSHtmlRenderer implements HtmlRenderer {

  private IOSRemoteProxy proxy;

  @SuppressWarnings("unused")
  private IOSHtmlRenderer() {
  }

  public IOSHtmlRenderer(IOSRemoteProxy proxy) {
    this.proxy = proxy;
  }

  public String renderSummary() {
    StringBuilder builder = new StringBuilder();
    builder.append("<fieldset>");
    builder.append("<legend>").append(proxy.getClass().getSimpleName()).append("</legend>");
    builder.append(proxy.getIOSNodeStatus()).append("<br/>");
    builder.append("Listening on ").append(proxy.getRemoteHost());
    if (proxy.getTimeOut() > 0) {
      int inSec = proxy.getTimeOut() / 1000;
      builder.append(". Test session time out after ").append(inSec).append(" sec.");
    }

    builder.append("<br>Supports up to <b>").append(proxy.getMaxNumberOfConcurrentTestSessions())
        .append("</b> concurrent tests from : </u><br>");


    for (TestSlot slot : proxy.getTestSlots()) {
      builder.append(slot.getCapabilities().containsKey(BROWSER) ? slot.getCapabilities().get(
          BROWSER) : slot.getCapabilities().get(APP));
      TestSession session = slot.getSession();
      builder.append(" [").append(slot.getCapabilities().get("supportedDevices")).append(" - ").append(slot.getCapabilities().get("sdkVersion")).append("]");
      builder.append(" : ").append(slot.getCapabilities().get("CFBundleExecutable"));
      builder.append(session == null ? " (free)" : "(busy, session " + session + ")");
      builder.append("<br/>");
    }
    if (proxy.getIOSNodeStatus() == IOSNodeStatus.Dirty) {
      //builder.append(proxy.getStatus().toString());
    }
    builder.append("</fieldset>");
    return builder.toString();
  }
}