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

import org.json.JSONObject;

public abstract class AbstractApplicationDataMessage extends BaseIOSWebKitMessage implements ApplicationDataMessage {

  protected String destination;

  protected JSONObject message;

  public AbstractApplicationDataMessage(String rawMessage) throws Exception {
    super(rawMessage);
    populateMessage();
    populateDestinationKey();
  }

  @Override
  public JSONObject getMessage() {
    return message;
  }

  @Override
  public String getDestinationKey() {
    return destination;
  }

  protected abstract void populateMessage() throws Exception;

  protected abstract void populateDestinationKey();

}
