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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dd.plist.NSDictionary;

public abstract class AbstractApplicationSentListingMessage extends BaseIOSWebKitMessage implements
    ApplicationSentListingMessage {

  protected String applicationIdentifier;

  protected List<WebkitPage> pages;

  private static final Logger log = Logger.getLogger(AbstractApplicationSentListingMessage.class.getName());

  public AbstractApplicationSentListingMessage(String rawMessage) throws Exception {
    super(rawMessage);
    pages = new ArrayList<>();
    populatePages();
    populateApplicationIdentifierKey();
    if (log.isLoggable(Level.FINE)) {
      log.fine("ApplicationSentListingMessage received: " + this);
    }
  }

  @Override
  public List<WebkitPage> getPages() {
    return pages;
  }

  @Override
  protected String toString(NSDictionary args) {
    StringBuilder builder = new StringBuilder();
    builder.append(pages.size() + " pages.\n\t");
    for (WebkitPage p : pages) {
      builder.append("[").append(p.getPageId()).append("], title: ").append(p.getTitle()).append(", url: ")
          .append(p.getURL()).append(" \t\tConnection:").append(p.getConnection()).append("\n\t");
    }
    return builder.toString();
  }

  @Override
  public boolean isPagesAvailable() {
    return pages.size() != 0;
  }

  @Override
  public String getApplicationIdentifier() {
    return applicationIdentifier;
  }

  protected abstract void populateApplicationIdentifierKey();

  protected abstract void populatePages();

}
