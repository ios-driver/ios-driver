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

import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;

public abstract class AbstractWebkitApplication implements WebkitApplication {

  protected String wirApplicationIdentifier;

  protected String wirApplicationName;

  protected boolean wirIsApplicationProxy;

  protected boolean isSafariApp;

  public AbstractWebkitApplication(NSDictionary nSDictionary) {
    wirApplicationIdentifier = nSDictionary.objectForKey(WIRAPPLICATIONIDENTIFIERKEY).toString();
    wirApplicationName = nSDictionary.objectForKey(WIRAPPLICATIONNAMEKEY).toString();
    NSNumber o = (NSNumber) nSDictionary.objectForKey(WIRISAPPLICATIONPROXYKEY);
    wirIsApplicationProxy = o.boolValue();
    isSafariApp = isSafariApp();
  }

  @Override
  public String getBundleId() {
    return wirApplicationIdentifier;
  }

  @Override
  public String getApplicationName() {
    return wirApplicationName;
  }

  @Override
  public boolean isSafari() {
    return isSafariApp;
  }

  protected abstract boolean isSafariApp();

}
