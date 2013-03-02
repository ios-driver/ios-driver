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

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.listeners.SelfHealingProxy;
import org.openqa.grid.internal.utils.HtmlRenderer;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

public class IOSRemoteProxy extends DefaultRemoteProxy implements SelfHealingProxy {

  private HtmlRenderer renderer = new IOSHtmlRenderer(this);

  public IOSRemoteProxy(RegistrationRequest request, Registry registry) {
    super(request, registry);
    IOSCapabilitiesMonitor iosCapabilitiesMonitor = new IOSCapabilitiesMonitor(this);
    new Thread(iosCapabilitiesMonitor).start();
  }

  @Override
  public HtmlRenderer getHtmlRender() {
    return renderer;
  }

}