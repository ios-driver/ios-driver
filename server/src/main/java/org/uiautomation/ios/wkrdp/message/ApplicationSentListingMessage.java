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

package org.uiautomation.ios.wkrdp.message;


import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationSentListingMessage extends BaseIOSWebKitMessage {

  private final List<WebkitPage> pages = new ArrayList<WebkitPage>();
  private static final Logger log = Logger.getLogger(ApplicationSentListingMessage.class.getName());

  public ApplicationSentListingMessage(String rawMessage) throws Exception {
    super(rawMessage);

    NSDictionary list = (NSDictionary) arguments.objectForKey("WIRListingKey");
    String[] keys = list.allKeys();

    for (String key : keys) {
      NSDictionary page = (NSDictionary) list.objectForKey(key);
      NSObject pageType = page.objectForKey("WIRTypeKey");
      if (pageType != null && pageType.toString().equals("WIRTypeWeb")) {
        pages.add(new WebkitPage(page));
      }
    }
    if (log.isLoggable(Level.FINE))
      log.fine("got: " + this);
  }

  public List<WebkitPage> getPages() {
    return pages;
  }

  @Override
  protected String toString(NSDictionary args) {
    StringBuilder builder = new StringBuilder();
    builder.append(pages.size() + " pages.\n\t");
    for (WebkitPage p : pages) {
      builder.append(
          "[" + p.getPageId() + "],title:" + p.getTitle() + ",url:" + p.getURL() + "\t\tConnection:"
          + p.getConnection() + "\n\t");
    }
    return builder.toString();
  }
}
