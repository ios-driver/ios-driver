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

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.dd.plist.NSData;

public class ApplicationDataMessageImpl extends AbstractApplicationDataMessage {

  public ApplicationDataMessageImpl(String rawMessage) throws Exception {
    super(rawMessage);
  }

  @Override
  protected void populateMessage() throws Exception {
    NSData data = (NSData) arguments.objectForKey(WIRMESSAGEDATAKEY);
    String encoded = data.getBase64EncodedData();
    byte[] bytes = Base64.decodeBase64(encoded);
    String s = new String(bytes);
    message = new JSONObject(s);
  }

  @Override
  protected void populateDestinationKey() {
    destination = arguments.objectForKey(WIRDDESTINATIONKEY).toString();
  }

}
