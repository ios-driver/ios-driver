/*
 * Copyright 2013 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios.wkrdp.message;

import com.dd.plist.NSData;
import com.dd.plist.NSDictionary;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.util.logging.Logger;


public class ApplicationDataMessage extends BaseIOSWebKitMessage {

  private final JSONObject message;
  private final String destinationKey;
  private static final Logger log = Logger.getLogger(ApplicationDataMessage.class.getName());

  public ApplicationDataMessage(String rawMessage) throws Exception {
    super(rawMessage);
    NSData data = (NSData) arguments.objectForKey("WIRMessageDataKey");
    String encoded = data.getBase64EncodedData();
    byte[] bytes = Base64.decodeBase64(encoded);
    String s = new String(bytes);
    message = new JSONObject(s);
    log.fine("got : " + rawMessage + "\n\tContent :" + message.toString());
    destinationKey = arguments.objectForKey("WIRDestinationKey").toString();
  }

  public JSONObject getMessage() {
    return message;
  }
   
  public String getDestinationKey() {
    return destinationKey;
  }

  @Override
  protected String toString(NSDictionary args) {
    return message.toString();
  }
}


