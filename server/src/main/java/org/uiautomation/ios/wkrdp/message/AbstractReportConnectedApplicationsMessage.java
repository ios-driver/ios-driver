/*
 * Copyright 2012-2015 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.wkrdp.message;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriverException;

public abstract class AbstractReportConnectedApplicationsMessage extends BaseIOSWebKitMessage implements
    ReportConnectedApplicationsMessage {

  protected List<WebkitApplication> applications;

  public AbstractReportConnectedApplicationsMessage(String rawMessage) throws Exception {
    super(rawMessage);
    applications = new ArrayList<>();
    populateApplications();
  }

  @Override
  public List<WebkitApplication> getApplications() {
    return applications;
  }

  @Override
  public WebkitApplication getApplication(String applicationIdentifier) {
    for (WebkitApplication application : applications) {
      if (application.getApplicationIdentifier().equals(applicationIdentifier)) {
        return application;
      }
    }
    throw new WebDriverException("Cannot find application with identifier: " + applicationIdentifier);
  }

  protected abstract void populateApplications();

}