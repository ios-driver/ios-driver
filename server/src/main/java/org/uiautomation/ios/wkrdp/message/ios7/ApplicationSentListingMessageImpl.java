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

package org.uiautomation.ios.wkrdp.message.ios7;

import org.uiautomation.ios.wkrdp.message.AbstractApplicationSentListingMessage;
import org.uiautomation.ios.wkrdp.message.WebkitPage;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;

public class ApplicationSentListingMessageImpl extends AbstractApplicationSentListingMessage {

  public ApplicationSentListingMessageImpl(String rawMessage) throws Exception {
    super(rawMessage);
  }

  @Override
  protected void populatePages() {
    NSDictionary list = (NSDictionary) arguments.objectForKey(WIRLISTINGKEY);
    String[] keys = list.allKeys();
    for (String key : keys) {
      NSDictionary page = (NSDictionary) list.objectForKey(key);
      pages.add(new WebkitPage(page));
    }
  }

  @Override
  protected void populateApplicationIdentifierKey() {
    applicationIdentifier = ((NSObject) arguments.objectForKey(WIRAPPLICATIONIDENTIFIERKEY)).toJavaObject().toString();
  }

}
